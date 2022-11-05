package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        User lastUser = getFakeData().findUserByAlias(request.getLastFolloweeAlias());
        User targetUser = getFakeData().findUserByAlias(request.getFollowerAlias());
        Pair<List<User>, Boolean> pageOfUsers = getFakeData().getPageOfUsers(lastUser, request.getLimit(), targetUser);
        return new FollowingResponse(pageOfUsers.getFirst(), pageOfUsers.getSecond());

    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        } else if (request.getLastFollowerAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a last follower alias");
        } else if (request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        User lastUser = getFakeData().findUserByAlias(request.getLastFollowerAlias());
        User targetUser = getFakeData().findUserByAlias(request.getFollowerAlias());
        Pair<List<User>, Boolean> pageOfUsers = getFakeData().getPageOfUsers(lastUser, request.getLimit(), targetUser);
        return new FollowersResponse(pageOfUsers.getFirst(), pageOfUsers.getSecond());
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if(request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        int count = getFakeData().getFakeUsers().size();

        return new FollowersCountResponse(count);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
