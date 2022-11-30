package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastStatus);

    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        try{
            FeedRequest request = new FeedRequest(authToken, getTargetUser().getAlias(), limit, getLastItem());
            FeedResponse response = getServerFacade().getFeed(request, "/getfeed");

            if(response.isSuccess()) {
                return new Pair<>(response.getStatuses(), response.getHasMorePages());
            } else {
                throw new RuntimeException("[Bad Request] Unable to retrieve feed");
            }
        }catch (IOException |TweeterRemoteException e){
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to retrieve feed");
        }
    }
}
