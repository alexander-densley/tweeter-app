package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends Response {
    private int followersCount;

    public FollowersCountResponse(int followersCount) {
        super(true, null);
        this.followersCount = followersCount;
    }

    public FollowersCountResponse(String message) {
        super(false, message);
    }

    public int getFollowersCount() {
        return followersCount;
    }
}
