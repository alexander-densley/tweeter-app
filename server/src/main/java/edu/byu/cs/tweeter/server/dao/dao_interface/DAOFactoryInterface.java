package edu.byu.cs.tweeter.server.dao.dao_interface;

public interface DAOFactoryInterface {
    AuthtokenDAOInterface makeAuthtokenDAO();
    UserDAOInterface makeUserDAO();
    FollowDAOInterface makeFollowDAO();
    StatusDAOInterface makeStatusDAO();
    FeedDAOInterface makeFeedDAO();
    S3DAOInterface makeS3DAO();
}
