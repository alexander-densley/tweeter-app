package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.DAOFactory;

public class UpdateFeedsHandler implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent sqsEvent, Context context) {
        Gson gson = new GsonBuilder().create();
        DAOFactory factory = new DAOFactory();

        for (SQSEvent.SQSMessage message : sqsEvent.getRecords()) {
            // Print out the body of the message
            System.out.println("Message Body: " + message.getBody());

            JsonObject messageBodyObject = JsonParser.parseString(message.getBody()).getAsJsonObject();

            JsonElement element = messageBodyObject.get("status");
            Status postedStatus = gson.fromJson(element, Status.class);

            JsonArray followers = messageBodyObject.getAsJsonArray("followers");

            List<String> followerAliases = new ArrayList<>();

            followers.forEach(follower -> followerAliases.add(follower.getAsString()));

            System.out.println("Status: " + postedStatus);
            System.out.println("Followers: " + Arrays.toString(followerAliases.toArray()));

            factory.makeFeedDAO().addFeedBatch(postedStatus, followerAliases);
        }
        return null;
    }
}
