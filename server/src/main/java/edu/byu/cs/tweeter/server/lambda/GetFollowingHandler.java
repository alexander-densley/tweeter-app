package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dao_interface.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;

/**
 * An AWS lambda function that returns the users a user is following.
 */
public class GetFollowingHandler implements RequestHandler<FollowingRequest, FollowingResponse> {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request.
     *
     * @param request contains the data required to fulfill the request.
     * @param context the lambda context.
     * @return the followees.
     */

    public static void main(String[] args)
    {
        AuthToken authToken = new AuthToken("c4031918-3467-4594-91d6-5c8406f9fa3d");
        FollowingRequest storyRequest = new FollowingRequest(authToken, "@densley", 10, null);
        GetFollowingHandler storyHandler = new GetFollowingHandler();
        FollowingResponse registerResponse = storyHandler.handleRequest(storyRequest, null);
    }
    @Override
    public FollowingResponse handleRequest(FollowingRequest request, Context context) {
        FollowService service = new FollowService(new DAOFactory());
        return service.getFollowees(request);
    }
}
