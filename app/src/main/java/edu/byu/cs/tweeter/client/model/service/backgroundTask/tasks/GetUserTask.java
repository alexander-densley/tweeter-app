package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {
    private static final String LOG_TAG = "GetUserTask";

    public static final String USER_KEY = "user";

    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private final String alias;

    private User user;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler, authToken);
        this.alias = alias;
    }

    private User getUser() {
        return getFakeData().findUserByAlias(alias);
    }


    @Override
    protected void runTask(){
//        user = getUser();
        try{
            GetUserRequest request = new GetUserRequest(alias, authToken);
            GetUserResponse response = getServerFacade().getUser(request, "/getuser");
            if(response.isSuccess()) {
                user = response.getUser();
                sendSuccessMessage();
            } else {
                throw new RuntimeException("[Bad Request] Unable to retrieve user");
            }
        }catch (IOException | TweeterRemoteException e){
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to retrieve user");
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }
}
