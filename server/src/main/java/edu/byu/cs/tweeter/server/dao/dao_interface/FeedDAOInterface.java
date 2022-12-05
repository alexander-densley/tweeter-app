package edu.byu.cs.tweeter.server.dao.dao_interface;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBStatus;

public interface FeedDAOInterface {
    FeedResponse getFeed(String alias, int limit, DynamoDBStatus lastStatus);
    void updateFeed(String alias, Status status);

    void addFeedBatch(Status postedStatus, List<String> followerAliases);
}
