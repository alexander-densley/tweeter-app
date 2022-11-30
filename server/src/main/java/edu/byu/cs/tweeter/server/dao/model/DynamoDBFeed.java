package edu.byu.cs.tweeter.server.dao.model;

import edu.byu.cs.tweeter.model.domain.Status;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class DynamoDBFeed {
    private String receiverAlias;
    private Long dateTime;
    private DynamoDBStatus dbStatus;

    public DynamoDBFeed() {
    }

    public DynamoDBFeed(String receiverAlias, Long dateTime, DynamoDBStatus dbStatus) {
        this.receiverAlias = receiverAlias;
        this.dateTime = dateTime;
        this.dbStatus = dbStatus;
    }

    @DynamoDbPartitionKey
    public String getReceiverAlias() {
        return receiverAlias;
    }

    public void setReceiverAlias(String receiverAlias) {
        this.receiverAlias = receiverAlias;
    }

    @DynamoDbSortKey
    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public DynamoDBStatus getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(DynamoDBStatus dbStatus) {
        this.dbStatus = dbStatus;
    }
}
