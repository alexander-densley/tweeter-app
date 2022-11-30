package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

/**
 * An AWS lambda function that handles get story requests. returns page of statuses
 */
public class GetStoryHandler implements RequestHandler<StoryRequest, StoryResponse> {
    public static void main(String[] args)
    {
        User user = new User("Alexander", "Densley","@densley", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String time = String.valueOf(timestamp.getTime());
        Status status = new Status("hey", user, time, new ArrayList<>(), new ArrayList<>());
        AuthToken authToken = new AuthToken("c4031918-3467-4594-91d6-5c8406f9fa3d");
        StoryRequest storyRequest = new StoryRequest(authToken,"@densley", 10, null);
        GetStoryHandler storyHandler = new GetStoryHandler();
        StoryResponse registerResponse = storyHandler.handleRequest(storyRequest, null);
    }

    @Override
    public StoryResponse handleRequest(StoryRequest request, Context context) {
        StatusService service = new StatusService(new DAOFactory());
        return service.getStory(request);
    }
}
