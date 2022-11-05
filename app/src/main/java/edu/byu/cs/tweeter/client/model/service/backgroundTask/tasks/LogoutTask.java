package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;
import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {
    private static final String LOG_TAG = "LogoutTask";



    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(messageHandler,authToken);
    }


    @Override
    protected void runTask() {
        try{
             LogoutRequest request = new LogoutRequest(authToken);
             LogoutResponse response = getServerFacade().logout(request, "/logout");

             if(response.isSuccess()) {
                 sendSuccessMessage();
             } else {
                 throw new RuntimeException("[Bad Request] Unable to logout user");
             }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to logout user");
        }
        sendSuccessMessage();
    }


}
