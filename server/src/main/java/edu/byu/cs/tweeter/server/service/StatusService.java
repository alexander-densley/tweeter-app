package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    public FeedResponse getFeed(FeedRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }
        else if (request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a limit greater than 0");
        }
        else if (request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an auth token");
        }
        else if (request.getLastStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a last status");
        }

        Pair<List<Status>, Boolean> pageOfStatuses = getFakeData().getPageOfStatus(request.getLastStatus(), request.getLimit());
        return new FeedResponse(pageOfStatuses.getFirst(), pageOfStatuses.getSecond());
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}
