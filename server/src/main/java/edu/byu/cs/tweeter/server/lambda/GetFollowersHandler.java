package edu.byu.cs.tweeter.server.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Timestamp;
import java.util.ArrayList;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersHandler implements RequestHandler<FollowersRequest, FollowersResponse> {
    public static void main(String[] args)
    {
        AuthToken authToken = new AuthToken("c4031918-3467-4594-91d6-5c8406f9fa3d");
        FollowersRequest storyRequest = new FollowersRequest(authToken, "@densley", 10, null);
        GetFollowersHandler storyHandler = new GetFollowersHandler();
        FollowersResponse registerResponse = storyHandler.handleRequest(storyRequest, null);
    }
    @Override
    public FollowersResponse handleRequest(FollowersRequest request, Context context) {
        FollowService service = new FollowService(new DAOFactory());
        return service.getFollowers(request);
    }
}
