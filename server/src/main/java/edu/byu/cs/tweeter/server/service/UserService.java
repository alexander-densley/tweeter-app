package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.dao_interface.DAOFactoryInterface;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    private DAOFactoryInterface daoFactory;

    public UserService(DAOFactoryInterface daoFactory) {
        this.daoFactory = daoFactory;
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        String correctPassword = daoFactory.makeUserDAO().getUserHash(request.getUsername());
        if(correctPassword == null) {
            return new LoginResponse("User not found");
        }
        if(correctPassword.equals(hashPassword(request.getPassword()))) {
            User user = daoFactory.makeUserDAO().login(request.getUsername(), request.getPassword());

            AuthToken authToken = daoFactory.makeAuthtokenDAO().insertAuthToken(user.getAlias());
            return new LoginResponse(user, authToken);
        } else {
            return new LoginResponse("Incorrect password");
        }
    }

    public RegisterResponse register(RegisterRequest request){
        if(request.getAlias() == null){
            throw new RuntimeException("[Bad Request] Missing a alias");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[Bad Request] Missing a first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing a last name");
        } else if(request.getImageUrl() == null) {
            throw new RuntimeException("[Bad Request] Missing an image URL");
        }


        String url = daoFactory.makeS3DAO().uploadProfileImage(request.getAlias(), request.getImageUrl());
        String hashedPassword = hashPassword(request.getPassword());

        User user = daoFactory.makeUserDAO().register(request.getAlias(), request.getFirstName(), request.getLastName(), url, hashedPassword);
        AuthToken authToken = daoFactory.makeAuthtokenDAO().insertAuthToken(user.getAlias());
        return new RegisterResponse(user, authToken);
    }
    public LogoutResponse logout(LogoutRequest request) {
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing an auth token");
        }
        daoFactory.makeAuthtokenDAO().removeAuthToken(request.getAuthToken());
        return new LogoutResponse();
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if (request.getAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing an alias");
        } else if (request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Missing an auth token");
        }
        User user = daoFactory.makeUserDAO().getUser(request.getAlias());
        return new GetUserResponse(user);
    }

    private static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }
}
