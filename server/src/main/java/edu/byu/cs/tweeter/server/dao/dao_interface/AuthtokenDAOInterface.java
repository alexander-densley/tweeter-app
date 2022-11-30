package edu.byu.cs.tweeter.server.dao.dao_interface;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.model.DynamoDBAuthtoken;

public interface AuthtokenDAOInterface {
    String getAuthTokenFromAlias(String alias);
    AuthToken insertAuthToken(String alias);
    void removeAuthToken(AuthToken authToken);
    String getNewAuthToken();
    String getAlias(String authToken);
    DynamoDBAuthtoken getAuthTokenFromToken(AuthToken authToken);
    Boolean isAuthTokenValid(AuthToken authToken);

}
