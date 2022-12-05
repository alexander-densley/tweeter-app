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
import software.amazon.awssdk.awscore.util.SignerOverrideUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
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
    public void updateFeed(String alias, Status status) {
        String dateTime = status.getDate();
        Long convertedDateTime = Long.valueOf(dateTime);

        DynamoDBStatus dynamoDBStatus = new DynamoDBStatus(status.getPost(), status.getUser().getAlias(), convertedDateTime, status.getUrls(), status.getMentions());
        DynamoDBFeed addedFeed = new DynamoDBFeed(alias, convertedDateTime, dynamoDBStatus);

        feedTable.putItem(addedFeed);
    }

    @Override
    public void addFeedBatch(Status postedStatus, List<String> followerAliases) {
        System.out.println("Adding feed batch");
        List<DynamoDBFeed> feedBatch = new ArrayList<>();
        for (String followerAlias : followerAliases) {
            DynamoDBFeed feed = new DynamoDBFeed(followerAlias, Long.valueOf(postedStatus.getDate()), new DynamoDBStatus(postedStatus.getPost(), postedStatus.getUser().getAlias(), Long.valueOf(postedStatus.getDate()), postedStatus.getUrls(), postedStatus.getMentions()));
            feedBatch.add(feed);
            if (feedBatch.size() == 25) {
                writeChunkOfFeed(feedBatch);
                feedBatch = new ArrayList<>();
            }
        }
        if (feedBatch.size() > 0) {
            writeChunkOfFeed(feedBatch);
        }
    }

    private void writeChunkOfFeed(List<DynamoDBFeed> feedBatch) {
        System.out.println("Writing chunk of feed");
        if(feedBatch.size() > 25) {
            throw new RuntimeException("feedBatch is too large");
        }
        WriteBatch.Builder<DynamoDBFeed> writeBatchBuilder = WriteBatch.builder(DynamoDBFeed.class).mappedTableResource(feedTable);
        for (DynamoDBFeed feed : feedBatch) {
            writeBatchBuilder.addPutItem(builder -> builder.item(feed));
        }
        BatchWriteItemEnhancedRequest writeBatchRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBatchBuilder.build()).build();

        try{
            System.out.println("Writing batch");
            BatchWriteResult result = enhancedClient.batchWriteItem(writeBatchRequest);
            if(result.unprocessedPutItemsForTable(feedTable).size() > 0) {
                writeChunkOfFeed(result.unprocessedPutItemsForTable(feedTable));
            }
        }catch (Exception e) {
            System.out.println("DIDNT WORK");
            e.printStackTrace();
        }
    }
}