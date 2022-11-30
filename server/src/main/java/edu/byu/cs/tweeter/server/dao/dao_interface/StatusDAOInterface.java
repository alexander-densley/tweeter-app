package edu.byu.cs.tweeter.server.dao.dao_interface;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBStatus;

public interface StatusDAOInterface {
    boolean postStatus(Status status);
    StoryResponse getStory(String userAlias, int limit, DynamoDBStatus lastStatus, User user);

}
