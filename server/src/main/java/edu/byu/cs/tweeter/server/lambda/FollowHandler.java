package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class FollowHandler implements RequestHandler<FollowRequest, FollowResponse> {
    public static void main(String[] args)
    {
        User user = new User("Robin", "Hood", "@robinhood", "testImageByteCode000000");
        AuthToken authToken = new AuthToken("85d24500-aa27-4072-b05d-d63ba7ff8438");
        FollowRequest request = new FollowRequest(user, authToken);
        FollowHandler handler = new FollowHandler();
        FollowResponse response = handler.handleRequest(request, null);
    }
    @Override
    public FollowResponse handleRequest(FollowRequest request, Context context) {
        FollowService service = new FollowService(new DAOFactory());
        return service.follow(request);
    }
}
