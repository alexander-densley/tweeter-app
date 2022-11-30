package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.dao_interface.StatusDAOInterface;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBAuthtoken;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBStatus;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoDBStatusDAO extends MainDAO implements StatusDAOInterface {
    DynamoDbTable<DynamoDBStatus> statusTable = enhancedClient.table("status", TableSchema.fromBean(DynamoDBStatus.class));

    @Override
    public boolean postStatus(Status status) {
        Long dateTime = Long.valueOf(status.getDate());
        try{
            DynamoDBStatus dynamoDBStatus = new DynamoDBStatus(status.getPost(), status.getUser().getAlias(), dateTime, status.getUrls(), status.getMentions());
            statusTable.putItem(dynamoDBStatus);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println("error posting status");
            return false;
        }

    }

    @Override
    public StoryResponse getStory(String senderAlias, int limit, DynamoDBStatus lastStatus, User user) {
        Key key = Key.builder().partitionValue(senderAlias).build();

        QueryEnhancedRequest.Builder queryBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit);

        if(lastStatus != null){
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("senderAlias", AttributeValue.builder().s(senderAlias).build());
            startKey.put("dateTime", AttributeValue.builder().n(Long.toString(lastStatus.getDateTime())).build());
            queryBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest queryRequest = queryBuilder.build();

        List<DynamoDBStatus> story = statusTable.query(queryRequest)
                .items()
                .stream()
                .limit(limit)
                .collect(Collectors.toList());

        if (story.size() < limit) {
            return new StoryResponse(convertToStatus(story,user), false);
        } else {
            return new StoryResponse(convertToStatus(story,user), true);
        }
    }

    private List<Status> convertToStatus(List<DynamoDBStatus> dynamoDBStatuses, User user) {
        List<Status> statuses = new ArrayList<>();
        for (DynamoDBStatus dynamoDBStatus : dynamoDBStatuses) {
            Status status = new Status(dynamoDBStatus.getPost(), user, Long.toString(dynamoDBStatus.getDateTime()), dynamoDBStatus.getUrls(), dynamoDBStatus.getMentions());
            statuses.add(status);
        }
        return statuses;
    }
}
