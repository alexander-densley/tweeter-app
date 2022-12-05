package edu.byu.cs.tweeter.server.dao;


import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.dao_interface.UserDAOInterface;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBUser;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;


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

//    public void addUserBatch(List<User> users) {
//        List<DynamoDBUser> batchToWrite = new ArrayList<>();
//        for (User u : users) {
//            DynamoDBUser dto = new DynamoDBUser(u.getAlias(), u.getFirstName(), u.getLastName(), u.getImageUrl(), "password");
//            batchToWrite.add(dto);
//
//            if (batchToWrite.size() == 25) {
//                // package this batch up and send to DynamoDB.
//                writeChunkOfUserDTOs(batchToWrite);
//                batchToWrite = new ArrayList<>();
//            }
//        }
//
//        // write any remaining
//        if (batchToWrite.size() > 0) {
//            // package this batch up and send to DynamoDB.
//            writeChunkOfUserDTOs(batchToWrite);
//        }
//    }
//    private void writeChunkOfUserDTOs(List<DynamoDBUser> userDTOs) {
//        if(userDTOs.size() > 25)
//            throw new RuntimeException("Too many users to write");
//
//        DynamoDbTable<DynamoDBUser> table = enhancedClient.table("users", TableSchema.fromBean(DynamoDBUser.class));
//        WriteBatch.Builder<DynamoDBUser> writeBuilder = WriteBatch.builder(DynamoDBUser.class).mappedTableResource(table);
//        for (DynamoDBUser item : userDTOs) {
//            writeBuilder.addPutItem(builder -> builder.item(item));
//        }
//        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
//                .writeBatches(writeBuilder.build()).build();
//
//        try {
//            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);
//
//            // just hammer dynamodb again with anything that didn't get written this time
//            if (result.unprocessedPutItemsForTable(table).size() > 0) {
//                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
//            }
//
//        } catch (DynamoDbException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
//        }
//    }

}
