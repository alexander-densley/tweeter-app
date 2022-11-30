package edu.byu.cs.tweeter.server.dao.dao_interface;

import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAOInterface {
    User login(String alias, String password);
    User register(String alias, String firstName, String lastName, String imageUrl, String password);
    User getUser(String alias);
    String getUserHash(String alias);
}
