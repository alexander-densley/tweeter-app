package edu.byu.cs.tweeter.model.net.response;

public class PostStatusResponse extends Response{
    public PostStatusResponse() {
        super(true, null);
    }

    public PostStatusResponse(String message) {
        super(false, message);
    }
}
