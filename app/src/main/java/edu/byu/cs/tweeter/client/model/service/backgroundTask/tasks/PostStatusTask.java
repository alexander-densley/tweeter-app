package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {
    private static final String LOG_TAG = "PostStatusTask";

    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";


    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;


    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(messageHandler, authToken);
        this.status = status;
    }

    @Override
    protected void runTask() {
        try{
            PostStatusRequest request = new PostStatusRequest(status, authToken);
            PostStatusResponse response = getServerFacade().postStatus(request, "/poststatus");

            if(response.isSuccess()) {
                sendSuccessMessage();
            } else {
                throw new RuntimeException("[Bad Request] Unable to post status");
            }
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to post status");
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {

    }

}
