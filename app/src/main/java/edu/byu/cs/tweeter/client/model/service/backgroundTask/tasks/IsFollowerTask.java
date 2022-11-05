package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {
    private static final String LOG_TAG = "IsFollowerTask";

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;
    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(messageHandler,authToken);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
//

        try{
            IsFollowerRequest request = new IsFollowerRequest(follower, followee, authToken);
            IsFollowerResponse response = getServerFacade().isFollower(request, "/isfollower");

            if(response.isSuccess()) {
                isFollower = response.getIsFollower();
//                isFollower = new Random().nextInt() > 0;
                sendSuccessMessage();
            } else {
                throw new RuntimeException("[Bad Request] Unable to determine if follower");
            }
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to determine if follower");
        }
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }



}
