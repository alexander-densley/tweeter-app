package edu.byu.cs.tweeter.client.model.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceTest {
    private User currentUser;
    private AuthToken currentAuthToken;

    private StatusService statusServiceSpy;
    private Status lastStatus;

    private GetItemsObserver observer;
    private CountDownLatch countDownLatch;

    private final int PAGE_SIZE = 10;

    @BeforeEach
    public void setup() {
        lastStatus = getFakeData().getFakeStatuses().get(0);

        currentUser = new User("FirstName", "LastName", null);
        currentAuthToken = new AuthToken();

        statusServiceSpy = Mockito.spy(new StatusService());

        observer = new GetItemsObserver();
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void testGetUserStory() throws InterruptedException {
        statusServiceSpy.loadMoreItemsStory(currentAuthToken, currentUser, PAGE_SIZE, lastStatus, observer);
        awaitCountDownLatch();

        //List<Status> expectedStatuses = getFakeData().getFakeStatuses().subList(0, 10);
        assertTrue(observer.isSuccess());
        assertNull(observer.getMessage());

        //List<Status> actualStatuses = observer.getStories();
        //assertArrayEquals(expectedStatuses.toArray(), actualStatuses.toArray());
        //assertEquals(expectedStatuses, observer.getStories());
        assertNotNull(observer.getStories());
        assertTrue(observer.getHasMorePages());
        assertNull(observer.getException());
    }

    private class GetItemsObserver implements PagedObserver<Status> {
        private boolean success;
        private String message;
        private List<Status> stories;
        private boolean hasMorePages;
        private Exception exception;

        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            this.success = true;
            this.message = null;
            this.stories = items;
            this.hasMorePages = hasMorePages;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            this.success = false;
            this.message = message;
            this.stories = null;
            this.hasMorePages = false;
            this.exception = null;

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception ex) {
            this.success = false;
            this.message = null;
            this.stories = null;
            this.hasMorePages = false;
            this.exception = ex;

            countDownLatch.countDown();
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Status> getStories() {
            return stories;
        }

        public boolean getHasMorePages() {
            return hasMorePages;
        }

        public Exception getException() {
            return exception;
        }
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