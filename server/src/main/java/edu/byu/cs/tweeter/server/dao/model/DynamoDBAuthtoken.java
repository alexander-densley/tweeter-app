package edu.byu.cs.tweeter.server.dao.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DynamoDBAuthtoken {
    private String authToken;
    private String alias;
    private String timestamp;

    public DynamoDBAuthtoken(){

    }

    public DynamoDBAuthtoken(String authToken, String alias, String timestamp) {
        this.authToken = authToken;
        this.alias = alias;
        this.timestamp = timestamp;
    }

    @DynamoDbPartitionKey
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
