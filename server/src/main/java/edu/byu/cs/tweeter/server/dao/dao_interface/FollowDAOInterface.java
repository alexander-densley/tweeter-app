package edu.byu.cs.tweeter.server.dao.dao_interface;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBFollows;

public interface FollowDAOInterface {
    int getFollowersCount(String alias, AuthToken authToken);
    int getFollowingCount(String alias, AuthToken authToken);
    void follow(AuthToken authToken, String aliasToFollow);
    void unfollow(AuthToken authToken, String aliasToUnfollow);
    boolean isFollower(String followerAlias, String followewAlias, AuthToken authToken);
    List<String> getFollowersByAlias(String alias);
    FollowingResponse getFollowing(FollowingRequest request);
    FollowersResponse getFollowers(FollowersRequest request);

}
