package edu.byu.cs.tweeter.server.dao;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class MainDAO {

    private static final Region region = Region.US_EAST_1;

    private static final DynamoDbClient ddb = DynamoDbClient.builder()
            .region(region).build();

    protected DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(ddb)
            .build();

    Boolean isAuthValid(AuthToken authToken) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime authTime = LocalDateTime.parse(authToken.getDatetime(), dtf);

        if(now.isBefore(authTime)){
            LocalDateTime newTime = authTime.plusHours(24);
            String newToken = dtf.format(newTime);
            authToken.setDatetime(newToken);
            return true;
        }
        else{
            return false;
        }
    }
}
