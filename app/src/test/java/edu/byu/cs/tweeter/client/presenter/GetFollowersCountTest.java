package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class GetFollowersCountTest {
    private User currentUser;
    private User lastFollower;
    private List<User> followers;
    private AuthToken currentAuthToken;
    private ServerFacade mockServerFacade;
    private FollowersCountRequest validRequest;
    private FollowersResponse successResponse;

    @BeforeEach
    public void setup() {

        currentUser = getFakeData().getFirstUser();
        lastFollower = getFakeData().getFirstUser();
        currentAuthToken = new AuthToken();

        followers = getFakeData().getFakeUsers();

        mockServerFacade = new ServerFacade();
        validRequest = new FollowersCountRequest(currentAuthToken, currentUser.getAlias());
    }

    @Test
    public void testGetFollowers() {
        try {
            FollowersCountResponse response = mockServerFacade.getFollowersCount(validRequest, "/getfollowerscount");
            Assertions.assertNotNull(response);
            Assertions.assertTrue(response.getFollowersCount() == 21);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    FakeData getFakeData() {
        return FakeData.getInstance();
    }
}