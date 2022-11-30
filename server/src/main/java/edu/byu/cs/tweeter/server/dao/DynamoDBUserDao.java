package edu.byu.cs.tweeter.server.dao;


import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dao_interface.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBUser;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;


public class DynamoDBUserDao extends MainDAO implements UserDAOInterface {
    DynamoDbTable<DynamoDBUser> userTable = enhancedClient.table("users", TableSchema.fromBean(DynamoDBUser.class));

    @Override
    public User login(String alias, String password) {
        Key key = Key.builder().partitionValue(alias).build();
        DynamoDBUser resultUser = userTable.getItem((GetItemEnhancedRequest.Builder builder) -> builder.key(key));

        if (resultUser == null) {
            System.out.println("User not found");
            return null;
        }

        User user = new User(resultUser.getFirstName(), resultUser.getLastName(),resultUser.getAlias(), resultUser.getImageUrl());
        return user;
    }

    @Override
    public User register(String alias, String firstName, String lastName, String imageUrl, String password) {
        DynamoDBUser newUser = new DynamoDBUser(alias, firstName, lastName, imageUrl, password);
        try{
            System.out.println("Adding a new item...");
            userTable.putItem(newUser);
        } catch (Exception e){
            System.out.println("AN ERROR" + e.getMessage());
        }
        User user = new User(newUser.getFirstName(), newUser.getLastName(), newUser.getAlias(), newUser.getImageUrl());
        return user;
    }

    @Override
    public User getUser(String alias) {
        Key key = Key.builder().partitionValue(alias).build();
        DynamoDBUser resultUser = userTable.getItem((GetItemEnhancedRequest.Builder builder) -> builder.key(key));

        if(resultUser == null){
            System.out.println("User not found");
            return null;
        }
        else {
            User user = new User(resultUser.getFirstName(), resultUser.getLastName(), resultUser.getAlias(), resultUser.getImageUrl());
            return user;
        }
    }

    @Override
    public String getUserHash(String alias) {
        Key key = Key.builder().partitionValue(alias).build();
        DynamoDBUser resultUser = userTable.getItem((GetItemEnhancedRequest.Builder builder) -> builder.key(key));

        if(resultUser == null){
            System.out.println("User not found");
            return null;
        }
        else {
            return resultUser.getPassword();
        }
    }

}
