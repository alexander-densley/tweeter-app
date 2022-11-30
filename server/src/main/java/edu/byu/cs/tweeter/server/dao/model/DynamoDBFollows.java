package edu.byu.cs.tweeter.server.dao.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class DynamoDBFollows {
    private String followerAlias;
    private String followeeAlias;

    public DynamoDBFollows(){}


    public DynamoDBFollows(String followerAlias, String followeeAlias) {
        this.followerAlias = followerAlias;
        this.followeeAlias = followeeAlias;
    }
    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = "followeeAlias-followerAlias-index")
    public String getFollowerAlias() {
        return followerAlias;
    }

    public void setFollowerAlias(String followerAlias) {
        this.followerAlias = followerAlias;
    }
    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = "followeeAlias-followerAlias-index")
    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }
}
