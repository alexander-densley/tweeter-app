package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.dao_interface.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBStatus;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    private DAOFactoryInterface daoFactory;

    public StatusService(DAOFactoryInterface daoFactory) {
        this.daoFactory = daoFactory;
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }
        else if (request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a limit greater than 0");
        }
        else if (request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }

        DynamoDBStatus lastStatus = null;
        if( request.getLastStatus() != null){
            lastStatus = convertStatusToDynamoDBStatus(request.getLastStatus());
        }

        FeedResponse response = daoFactory.makeFeedDAO().getFeed(request.getUserAlias(), request.getLimit(), lastStatus);
        return response;
    }

    public StoryResponse getStory(StoryRequest request){
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }
        else if (request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a limit greater than 0");
        }
        else if (request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }

        DynamoDBStatus lastStatus = null;
        if( request.getLastStatus() != null){
             lastStatus = convertStatusToDynamoDBStatus(request.getLastStatus());
        }

        StoryResponse response = daoFactory.makeStatusDAO().getStory(request.getUserAlias(), request.getLimit(), lastStatus, daoFactory.makeUserDAO().getUser(request.getUserAlias()));
        return response;
    }

    private DynamoDBStatus convertStatusToDynamoDBStatus(Status status){
        if(status == null){
            return null;
        }
        return new DynamoDBStatus(status.getPost(), status.getUser().getAlias(), Long.valueOf(status.getDate()), status.getUrls(), status.getMentions());
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        if(request.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }
        else if (request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }

        boolean validAuth = daoFactory.makeAuthtokenDAO().isAuthTokenValid(request.getAuthToken());
        if(!validAuth){
            return new PostStatusResponse("Invalid auth token");
        }
        boolean statusPosted = daoFactory.makeStatusDAO().postStatus(request.getStatus());

        if(statusPosted){
            List<String> alias = daoFactory.makeFollowDAO().getFollowersByAlias(request.getStatus().getUser().getAlias());
            for(String follower : alias){
                daoFactory.makeFeedDAO().updateFeed(follower, request);
            }
            return new PostStatusResponse();
        }
        else{
            return new PostStatusResponse("Failed to post status");
        }
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
