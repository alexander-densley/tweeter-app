package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

/**
 * An AWS lambda function that registers a user  and returns the user object and an auth code for
 * a successful register.
 */
public class RegisterHandler implements RequestHandler<RegisterRequest, RegisterResponse> {
    public static void main(String[] args)
    {
        RegisterRequest registerRequest = new RegisterRequest(
                "Robin2", "Hood2", "@robinhood2", "voyager_is_the_best2",
                "testImageByteCode000009");
        RegisterHandler registerHandler = new RegisterHandler();
        RegisterResponse registerResponse = registerHandler.handleRequest(registerRequest, null);
    }
    @Override
    public RegisterResponse handleRequest(RegisterRequest request, Context context) {
        UserService userService = new UserService(new DAOFactory());
        return userService.register(request);
    }
}
