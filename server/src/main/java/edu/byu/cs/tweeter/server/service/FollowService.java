package edu.byu.cs.tweeter.server.service;

import java.util.List;
import java.util.Random;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dao_interface.DAOFactoryInterface;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    private DAOFactoryInterface daoFactory;

    public FollowService(DAOFactoryInterface daoFactory) {
        this.daoFactory = daoFactory;
    }

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
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        FollowingResponse response = daoFactory.makeFollowDAO().getFollowing(request);
        return response;

    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        FollowersResponse response = daoFactory.makeFollowDAO().getFollowers(request);
        return response;
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        if (request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        int count = daoFactory.makeFollowDAO().getFollowersCount(request.getTargetAlias(), request.getAuthToken());

        return new FollowersCountResponse(count);
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        if (request.getTargetAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        int count = daoFactory.makeFollowDAO().getFollowingCount(request.getTargetAlias(), request.getAuthToken());

        return new FollowingCountResponse(count);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        try {
            daoFactory.makeFollowDAO().unfollow(request.getAuthToken(), request.getFollowee().getAlias());
            return new UnfollowResponse();
        } catch (Exception e) {
            return new UnfollowResponse("error unfollowing user");
        }
    }

    public FollowResponse follow(FollowRequest request) {
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        try {
            daoFactory.makeFollowDAO().follow(request.getAuthToken(), request.getFollowee().getAlias());
            return new FollowResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return new FollowResponse("Error following user");
        }
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if (request.getFollowee() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee");
        } else if (request.getFollower() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        boolean followStatus = daoFactory.makeFollowDAO().isFollower(request.getFollower().getAlias(), request.getFollowee().getAlias(), request.getAuthToken());
        return new IsFollowerResponse(followStatus);
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
}

