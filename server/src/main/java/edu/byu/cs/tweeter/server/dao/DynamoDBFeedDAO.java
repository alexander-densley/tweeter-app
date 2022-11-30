package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.dao_interface.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBFeed;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBStatus;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBFeedDAO extends MainDAO implements FeedDAOInterface {
    DynamoDbTable<DynamoDBFeed> feedTable = enhancedClient.table("feed", TableSchema.fromBean(DynamoDBFeed.class));

    @Override
    public FeedResponse getFeed(String alias, int limit, DynamoDBStatus lastStatus) {

        Key key = Key.builder().partitionValue(alias).build();

        QueryEnhancedRequest.Builder queryBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(lastStatus != null){
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("receiverAlias", AttributeValue.builder().s(alias).build());
            startKey.put("dateTime", AttributeValue.builder().n(Long.toString(lastStatus.getDateTime())).build());
            queryBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = queryBuilder.build();

        List<DynamoDBFeed> feed = feedTable.query(queryRequest)
                .items()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());

        if (feed.size() < limit) {
            return new FeedResponse(convertToStatus(feed), false);
        } else {
            return new FeedResponse(convertToStatus(feed), true);
        }
    }
    private List<Status> convertToStatus(List<DynamoDBFeed> dynamoDBStatuses) {
        List<Status> statuses = new ArrayList<>();
        for (DynamoDBFeed dynamoDBFeed : dynamoDBStatuses) {
            DynamoDBStatus dynamoDBStatus = dynamoDBFeed.getDbStatus();
            String alias = dynamoDBStatus.getSenderAlias();
            DynamoDBUserDao userDao = new DynamoDBUserDao();
            User user = userDao.getUser(alias);
            Status status = new Status(dynamoDBStatus.getPost(), user, Long.toString(dynamoDBStatus.getDateTime()), dynamoDBStatus.getUrls(), dynamoDBStatus.getMentions());
            statuses.add(status);
        }
        return statuses;
    }

    @Override
    public void updateFeed(String alias, PostStatusRequest request) {
        Status status = request.getStatus();
        String dateTime = status.getDate();
        Long convertedDateTime = Long.valueOf(dateTime);

        DynamoDBStatus dynamoDBStatus = new DynamoDBStatus(status.getPost(), status.getUser().getAlias(), convertedDateTime, status.getUrls(), status.getMentions());
        DynamoDBFeed addedFeed = new DynamoDBFeed(alias, convertedDateTime, dynamoDBStatus);

        feedTable.putItem(addedFeed);
    }
}