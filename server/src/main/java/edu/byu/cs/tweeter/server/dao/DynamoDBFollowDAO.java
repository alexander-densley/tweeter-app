package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.dao_interface.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBFeed;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBFollows;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBUser;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBFollowDAO extends MainDAO implements FollowDAOInterface {
    DynamoDbTable<DynamoDBFollows> followsTable = enhancedClient.table("follows", TableSchema.fromBean(DynamoDBFollows.class));
    DynamoDbIndex<DynamoDBFollows> followsIndex = followsTable.index("followeeAlias-followerAlias-index");

    @Override
    public int getFollowersCount(String alias, AuthToken authToken) {
        if(!isAuthValid(authToken)){
            throw new RuntimeException("Invalid auth token");
        }
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        QueryEnhancedRequest queryRequest = request.build();

        List<DynamoDBFollows> follows = new ArrayList<>();;
        SdkIterable<Page<DynamoDBFollows>> results = followsIndex.query(queryRequest);
        for (Page<DynamoDBFollows> page : results) {
            follows.addAll(page.items());
        }
        return follows.size();
    }

    @Override
    public int getFollowingCount(String alias, AuthToken authToken) {
        if(!isAuthValid(authToken)){
            throw new RuntimeException("Invalid auth token");
        }
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(true).build();
        List<DynamoDBFollows> follows = followsTable.query(request).items().stream().collect(Collectors.toList());
        return follows.size();
    }

    @Override
    public void follow(AuthToken authToken, String aliasToFollow) {
        if(!isAuthValid(authToken)){
            throw new RuntimeException("Invalid auth token");
        }
        String curUserAlias = new DynamoDBAuthtokenDAO().getAlias(authToken.getToken());
        DynamoDBFollows newFollow = new DynamoDBFollows(curUserAlias, aliasToFollow);
        try{
            followsTable.putItem(newFollow);
        }catch(Exception e){
            System.out.println("Error adding follow");
        }

    }

    @Override
    public void unfollow(AuthToken authToken, String aliasToUnfollow) {
        if(!isAuthValid(authToken)){
            throw new RuntimeException("Invalid auth token");
        }
        String curUserAlias = new DynamoDBAuthtokenDAO().getAlias(authToken.getToken());
        DynamoDBFollows newUnFollow = new DynamoDBFollows(curUserAlias, aliasToUnfollow);
        try{
            followsTable.deleteItem(newUnFollow);
        }catch(Exception e){
            System.out.println("Error deleting follow");
        }
    }

    @Override
    public boolean isFollower(String followerAlias, String followeeAlias, AuthToken authToken) {
        if(!isAuthValid(authToken)){
            throw new RuntimeException("Invalid auth token");
        }
        Key key = Key.builder().partitionValue(followerAlias).sortValue(followeeAlias).build();
        DynamoDBFollows follow = followsTable.getItem((GetItemEnhancedRequest.Builder builder) -> builder.key(key));

        return follow != null;
    }

    @Override
    public List<String> getFollowersByAlias(String alias) {
        Key key = Key.builder()
                .partitionValue(alias)
                .build();

        QueryEnhancedRequest.Builder request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key));
        QueryEnhancedRequest queryRequest = request.build();

        List<DynamoDBFollows> follows = new ArrayList<>();

        SdkIterable<Page<DynamoDBFollows>> results = followsIndex.query(queryRequest);
        PageIterable<DynamoDBFollows> pages = PageIterable.create(results);

        pages.stream()
                .limit(1)
                .forEach(page -> follows.addAll(page.items()));

        List<String> followersAlias = new ArrayList<>();

        for(DynamoDBFollows follow : follows){
            followersAlias.add(follow.getFollowerAlias());
        }
        return followersAlias;
    }

    @Override
    public FollowingResponse getFollowing(FollowingRequest request) {
        if(!isAuthValid(request.getAuthToken())){
            return new FollowingResponse("Invalid auth token. please lgout and login again");
        }
        Key key = Key.builder()
                .partitionValue(request.getFollowerAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(true);

        if(request.getLastFolloweeAlias() != null){
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("followerAlias", AttributeValue.builder().s(request.getFollowerAlias()).build());
            startKey.put("followeeAlias", AttributeValue.builder().s(request.getLastFolloweeAlias()).build());
            requestBuilder.exclusiveStartKey(startKey);
        }
        QueryEnhancedRequest queryRequest = requestBuilder.build();

        try {
            List<DynamoDBFollows> results = followsTable
                    .query(queryRequest)
                    .items()
                    .stream()
                    .limit(request.getLimit() + 1)
                    .collect(Collectors.toList());
            List<User> followers = new ArrayList<>();

            Boolean hasMorePages = false;
            if(results.size() > request.getLimit()){
                hasMorePages = true;
                results.remove(results.size() - 1);
            }

            for (DynamoDBFollows follow : results) {
                QueryConditional queryConditional = QueryConditional
                        .keyEqualTo(Key.builder()
                                .partitionValue(follow.getFolloweeAlias())
                                .build());
                DynamoDbTable<DynamoDBUser> usersTable = enhancedClient.table("users", TableSchema.fromBean(DynamoDBUser.class));
                Iterator<DynamoDBUser> users = usersTable.query(queryConditional)
                        .items().iterator();
                if (users.hasNext()) {
                    DynamoDBUser user = users.next();
                    followers.add(new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImageUrl()));
                }
            }
            return new FollowingResponse(followers, hasMorePages);
        }catch (Exception e){
            System.out.println("Error getting following");
            return null;
        }
    }

    @Override
    public FollowersResponse getFollowers(FollowersRequest request) {
        if(!isAuthValid(request.getAuthToken())){
            return new FollowersResponse("Invalid auth token. please lgout and login again");
        }
        Key key = Key.builder()
                .partitionValue(request.getFollowerAlias())
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .scanIndexForward(false)
                .limit(request.getLimit() + 1);

        if(request.getLastFollowerAlias() != null){
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("followeeAlias", AttributeValue.builder().s(request.getFollowerAlias()).build());
            startKey.put("followerAlias", AttributeValue.builder().s(request.getLastFollowerAlias()).build());
            requestBuilder.exclusiveStartKey(startKey);
        }
        QueryEnhancedRequest queryRequest = requestBuilder.build();

        try {
            SdkIterable<Page<DynamoDBFollows>> results = followsIndex.query(queryRequest);
            PageIterable<DynamoDBFollows> pages = PageIterable.create(results);
            List<DynamoDBFollows> resultsList = new ArrayList<>();
            pages.stream()
                    .limit(1)
                    .forEach(page -> resultsList.addAll(page.items()));

            List<User> followers = new ArrayList<>();

            Boolean hasMorePages = false;
            if(resultsList.size() > request.getLimit()){
                hasMorePages = true;
                resultsList.remove(resultsList.size() - 1);
            }

            for (DynamoDBFollows follow : resultsList) {
                QueryConditional queryConditional = QueryConditional
                        .keyEqualTo(Key.builder()
                                .partitionValue(follow.getFollowerAlias())
                                .build());

                DynamoDbTable<DynamoDBUser> usersTable = enhancedClient.table("users", TableSchema.fromBean(DynamoDBUser.class));
                Iterator<DynamoDBUser> users = usersTable.query(queryConditional)
                        .items().iterator();
                if (users.hasNext()) {
                    DynamoDBUser user = users.next();
                    followers.add(new User(user.getFirstName(), user.getLastName(), user.getAlias(), user.getImageUrl()));
                }
            }
            return new FollowersResponse(followers, hasMorePages);
        }catch (Exception e){
            System.out.println("Error getting followers");
            return null;
        }
    }
}
