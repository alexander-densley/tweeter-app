package edu.byu.cs.tweeter.server.dao.model;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class DynamoDBStatus {
    private String post;
    private String senderAlias;
    private Long dateTime;
    private List<String> urls;
    private List<String> mentions;

    public DynamoDBStatus() {
    }

    public DynamoDBStatus(String post, String senderAlias, Long dateTime, List<String> urls, List<String> mentions) {
        this.post = post;
        this.senderAlias = senderAlias;
        this.dateTime = dateTime;
        this.urls = urls;
        this.mentions = mentions;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
    @DynamoDbPartitionKey
    public String getSenderAlias() {
        return senderAlias;
    }

    public void setSenderAlias(String senderAlias) {
        this.senderAlias = senderAlias;
    }
    @DynamoDbSortKey
    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }
}
