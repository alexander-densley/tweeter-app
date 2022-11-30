package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class RegisterTest {
    private ServerFacade mockServerFacade;
    private RegisterRequest validRequest;
    private String firstName;
    private String lastName;
    private String image;
    private String username;
    private String password;

    @BeforeEach
    public void setup() {
        firstName = "firstName";
        lastName = "lastName";
        image = "image";
        username = "username";
        password = "password";
        validRequest = new RegisterRequest(firstName, lastName, username, password, image);
        mockServerFacade = new ServerFacade();
    }

    @Test
    public void testGetFollowers() {
        try {
            RegisterResponse response = mockServerFacade.register(validRequest, "/register");
            Assertions.assertNotNull(response);
            Assertions.assertTrue(response.getUser().getAlias().equals("@allen"));
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}