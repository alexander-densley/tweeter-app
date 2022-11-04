package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface IsFollowObserver extends ServiceObserver{
    void handleSuccess(boolean isFollower);

}
