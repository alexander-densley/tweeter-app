package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.dao_interface.AuthtokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.dao_interface.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.dao_interface.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.dao_interface.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.dao_interface.S3DAOInterface;
import edu.byu.cs.tweeter.server.dao.dao_interface.StatusDAOInterface;
import edu.byu.cs.tweeter.server.dao.dao_interface.UserDAOInterface;

public class DAOFactory implements DAOFactoryInterface {
    @Override
    public AuthtokenDAOInterface makeAuthtokenDAO() {
        return new DynamoDBAuthtokenDAO();
    }

    @Override
    public UserDAOInterface makeUserDAO() {
        return new DynamoDBUserDao();
    }

    @Override
    public FollowDAOInterface makeFollowDAO() {
        return new DynamoDBFollowDAO();
    }

    @Override
    public StatusDAOInterface makeStatusDAO() {
        return new DynamoDBStatusDAO();
    }

    @Override
    public FeedDAOInterface makeFeedDAO() {
        return new DynamoDBFeedDAO();
    }

    @Override
    public S3DAOInterface makeS3DAO() {
        return new S3DAO();
    }
}
