package edu.byu.cs.tweeter.server.dao.dao_interface;

public interface S3DAOInterface {
    String uploadProfileImage(String alias, String image);
}
