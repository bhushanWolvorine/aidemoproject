package com.aidemoproject;



import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.Instant;
import java.util.List;

public class MongoJourneyLogger {

 private static final String DB_NAME = "ai-test-logs";
 private static final String COLLECTION = "user_journeys";
 private final MongoCollection<Document> collection;

 public MongoJourneyLogger() {
     MongoClient client = MongoClients.create("mongodb://localhost:27017");
     MongoDatabase db = client.getDatabase(DB_NAME);
     this.collection = db.getCollection(COLLECTION);
     System.out.println("Connected to MongoDB → ai-test-logs.user_journeys");
 }

 public void logJourney(String testName, String journeyType, List<String> conversation, boolean passed) {
     Document doc = new Document()
         .append("test_name", testName)
         .append("journey_type", journeyType)
         .append("conversation", conversation)
         .append("passed", passed)
         .append("timestamp", Instant.now().toString())
         .append("environment", "test")
         .append("runner", System.getProperty("user.name"));

     collection.insertOne(doc);
     System.out.println("Journey logged to MongoDB: " + journeyType + " → " + (passed ? "PASSED" : "FAILED"));
 }
}