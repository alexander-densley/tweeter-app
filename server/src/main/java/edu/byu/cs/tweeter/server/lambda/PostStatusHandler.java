package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Timestamp;
import java.util.ArrayList;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that handles post status requests.
 */
public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {
    public static void main(String[] args)
    {
        User user = new User("Alexander", "Densley","@densley", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = String.valueOf(timestamp.getTime());
        Status status = new Status("hey", user, time, new ArrayList<>(), new ArrayList<>());
        AuthToken authToken = new AuthToken("c4031918-3467-4594-91d6-5c8406f9fa3d");
        PostStatusRequest storyRequest = new PostStatusRequest(status, authToken);
        PostStatusHandler storyHandler = new PostStatusHandler();
        PostStatusResponse registerResponse = storyHandler.handleRequest(storyRequest, null);
    }
    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        StatusService service = new StatusService(new DAOFactory());
        return service.postStatus(request);
    }
}
