package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {
    private static final String LOG_TAG = "GetFollowersTask";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastFollower);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        try{
            String alias;
            if(getLastItem() == null){
                alias = null;
            }
            else{
                alias = getLastItem().getAlias();
            }
            FollowersRequest request = new FollowersRequest(authToken, getTargetUser().getAlias(), limit, alias);
            FollowersResponse response = getServerFacade().getFollowers(request, "/getfollowers");

            if(response.isSuccess()) {
                return new Pair<>(response.getFollowers(), response.getHasMorePages());
            } else {
                throw new RuntimeException("[Bad Request] Unable to retrieve followers");
            }

        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to retrieve followers");
        }

    }
}
