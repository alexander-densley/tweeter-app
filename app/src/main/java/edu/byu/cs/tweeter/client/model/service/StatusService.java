package edu.byu.cs.tweeter.client.model.service;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PageNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.tasks.PostStatusTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service{


    public void postStatus(AuthToken currUserAuthToken, User currUser, String post, String formattedDateTime, List<String> parseURLs, List<String> parseMentions, SimpleNotificationObserver postStatusObserver){
        Status newStatus = new Status(post, currUser, formattedDateTime, parseURLs, parseMentions);
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken,
                newStatus, new SimpleNotificationHandler(postStatusObserver));
        runTask(statusTask);
    }



    public void loadMoreItemsStory(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver getStoryObserver){
        GetStoryTask getUserTask = new GetStoryTask(currUserAuthToken,
                user, pageSize, lastStatus, new PageNotificationHandler<Status>(getStoryObserver));
        runTask(getUserTask);
    }


    public void loadMoreItemsFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver getFeedObserver){
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new PageNotificationHandler<Status>(getFeedObserver));
        runTask(getFeedTask);
    }

}