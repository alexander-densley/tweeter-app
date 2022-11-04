package edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    private static final String LOG_TAG = "GetStoryTask";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastStatus);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
//        return getFakeData().getPageOfStatus(getLastItem(), getLimit());
        if(getLastItem() == null){
            lastItem = getFakeData().getFakeStatuses().get(0);
        }
        try{
            StoryRequest request = new StoryRequest(authToken, getTargetUser().getAlias(), limit, getLastItem());
            StoryResponse response = getServerFacade().getStory(request, "/getstory");

            if(response.isSuccess()) {
                return new Pair<>(response.getStatuses(), response.getHasMorePages());
            } else {
                throw new RuntimeException("[Bad Request] Unable to retrieve story");
            }
        } catch (IOException | TweeterRemoteException e) {
            e.printStackTrace();
            Log.e(BackgroundTask.EXCEPTION_KEY, e.getMessage(), e);
            throw new RuntimeException("[Server Error] Unable to retrieve story");
        }
    }


}