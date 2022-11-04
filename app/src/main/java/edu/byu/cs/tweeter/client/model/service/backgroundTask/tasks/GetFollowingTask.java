package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {
    private static final String LOG_TAG = "GetFollowingTask";

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(messageHandler,authToken, targetUser, limit, lastFollowee);

    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        if(getLastItem() == null) {
            lastItem = getFakeData().getFirstUser();
        }
        try{
            FollowingRequest request = new FollowingRequest(authToken, getTargetUser().getAlias(), limit, getLastItem().getAlias());
            FollowingResponse response = getServerFacade().getFollowees(request, "/getfollowing");

            if(response.isSuccess()) {
                return new Pair<>(response.getFollowees(), response.getHasMorePages());
            } else {
                throw new RuntimeException("[Bad Request] Unable to retrieve followees");
            }

        } catch (IOException | TweeterRemoteException  e) {
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to retrieve followews");
        }

//        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
    }
}
