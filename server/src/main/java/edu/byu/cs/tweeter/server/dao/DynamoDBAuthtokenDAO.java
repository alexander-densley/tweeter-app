package edu.byu.cs.tweeter.server.dao;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.dao_interface.AuthtokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBAuthtoken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

public class DynamoDBAuthtokenDAO extends MainDAO implements AuthtokenDAOInterface {
    DynamoDbTable<DynamoDBAuthtoken> authtokenTable = enhancedClient.table("authtoken", TableSchema.fromBean(DynamoDBAuthtoken.class));
    @Override
    public String getAuthTokenFromAlias(String alias) {

        return null;
    }

    @Override
    public AuthToken insertAuthToken(String alias) {
        String auth = UUID.randomUUID().toString();
        String timestamp = LocalDate.now().toString();
        DynamoDBAuthtoken newAuthtoken = new DynamoDBAuthtoken(auth, alias, timestamp);
        authtokenTable.putItem(newAuthtoken);
        AuthToken authToken = new AuthToken(auth);
        return authToken;
    }

    @Override
    public void removeAuthToken(AuthToken authToken) {
        Key key = Key.builder().partitionValue(authToken.getToken()).build();
        DynamoDBAuthtoken result = authtokenTable.getItem((GetItemEnhancedRequest.Builder builder) -> builder.key(key));
        if(result != null){
            authtokenTable.deleteItem(key);
        }
        else {
            System.out.println("AuthToken not found");
        }
    }

    @Override
    public String getNewAuthToken() {
        return null;
    }

    @Override
    public String getAlias(String authToken) {
        Key key = Key.builder().partitionValue(authToken).build();
        DynamoDBAuthtoken result = authtokenTable.getItem((GetItemEnhancedRequest.Builder builder) -> builder.key(key));
        if(result != null){
            return result.getAlias();
        }
        else {
            System.out.println("AuthToken not found");
            return null;
        }
    }

    @Override
    public DynamoDBAuthtoken getAuthTokenFromToken(AuthToken authToken) {

        String token = authToken.getToken();
        Key key = Key.builder().partitionValue(token).build();
        DynamoDBAuthtoken result = authtokenTable.getItem((GetItemEnhancedRequest.Builder builder) -> builder.key(key));
        if(result != null){
            return result;
        }
        else {
            System.out.println("AuthToken not found");
            return null;
        }
    }

    @Override
    public Boolean isAuthTokenValid(AuthToken authToken) {
        try{
            DynamoDBAuthtoken dynamoDBAuthtoken = getAuthTokenFromToken(authToken);
            LocalDate currentDate = LocalDate.now();
            LocalDate tokenDate = LocalDate.parse(dynamoDBAuthtoken.getTimestamp());

            Period period = Period.between(currentDate, tokenDate);

            return period.getDays() >= -1;
        }
        catch(Exception e){
            System.out.println("AuthToken not found");
            System.out.println(e.getMessage());
            return false;
        }
    }
}
