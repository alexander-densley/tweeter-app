package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    private static final String LOG_TAG = "UnfollowTask";



    /**
     * The user that is being followed.
     */
    private final User followee;


    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler,authToken);
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {
            UnfollowRequest request = new UnfollowRequest(followee, authToken);
            UnfollowResponse response = getServerFacade().unfollow(request, "/unfollow");

            if (response.isSuccess()) {
                sendSuccessMessage();
            } else {
                throw new RuntimeException("[Bad Request] Unable to unfollow user");
            }

        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to unfollow user");
        }
    }


}
