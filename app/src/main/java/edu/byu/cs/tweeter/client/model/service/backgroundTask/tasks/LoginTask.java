package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    private static final String LOG_TAG = "LoginTask";

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }


    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {
        if(getAuthenticatedUser() == null){
            authenticatedUser = getFakeData().getFirstUser();
        }
        try{
            LoginRequest request = new LoginRequest(username, password);
            LoginResponse response = getServerFacade().login(request, "/login");
            if(response.isSuccess()){
                return new Pair<>(response.getUser(), response.getAuthToken());
            }else{
                throw new RuntimeException("[Bad Request] Unable to login");
            }
        }catch (IOException | TweeterRemoteException e){
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to login");
        }
    }
}
