package net.codejava.aws;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import java.util.HashMap;

public class queryitem {

    public static void main(String[] args) {


       String tableName = "test";
       String partitionKeyName = "userid";
       String partitionKeyVal = "04";

       String partitionAlias = "#ids";

       System.out.format("Querying %s", tableName);
       System.out.println("");

       ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
       Region region = Region.EU_WEST_1;
       DynamoDbClient ddb = DynamoDbClient.builder()
           .credentialsProvider(credentialsProvider)
           .region(region)
           .build();

       int count = queryTable(ddb, tableName, partitionKeyName, partitionKeyVal,partitionAlias ) ;
       System.out.println("There were "+count + "  record(s) returned");
       ddb.close();
    }

    public static int queryTable(DynamoDbClient ddb, String tableName, String partitionKeyName, String partitionKeyVal, String partitionAlias) {

        // Set up an alias for the partition key name in case it's a reserved word.
        HashMap<String,String> attrNameAlias = new HashMap<String,String>();
        attrNameAlias.put(partitionAlias, partitionKeyName);

        // Set up mapping of the partition name with the value.
        HashMap<String, AttributeValue> attrValues = new HashMap<>();

        attrValues.put(":"+partitionKeyName, AttributeValue.builder()
            .s(partitionKeyVal)
            .build());

        QueryRequest queryReq = QueryRequest.builder()
            .tableName(tableName)
            .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
            .expressionAttributeNames(attrNameAlias)
            .expressionAttributeValues(attrValues)
            .build();

        try {
            QueryResponse response = ddb.query(queryReq);
            return response.count();

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return -1;
    }
}