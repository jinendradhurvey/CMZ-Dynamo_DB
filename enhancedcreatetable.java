package net.codejava.aws;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

public class enhancedcreatetable {
	
	 public static void main(String[] args) {

	        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
	        Region region = Region.EU_WEST_1;
	        DynamoDbClient ddb = DynamoDbClient.builder()
	            .credentialsProvider(credentialsProvider)
	            .region(region)
	            .build();

	        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
	            .dynamoDbClient(ddb)
	            .build();

	        createTable(enhancedClient);
	        ddb.close();
	    }
	
    public static void createTable(DynamoDbEnhancedClient  enhancedClient) {
        // Create a DynamoDbTable object
        DynamoDbTable<customer> customerTable = enhancedClient.table("Customer", TableSchema.fromBean(customer.class));
        // Create the table
        customerTable.createTable(builder -> builder
                .provisionedThroughput(b -> b
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
        );

        System.out.println("Waiting for table creation...");

        try (DynamoDbWaiter waiter = DynamoDbWaiter.create()) { // DynamoDbWaiter is Autocloseable
            ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("Customer").build())
                    .matched();
            DescribeTableResponse tableDescription = response.response().orElseThrow(
                    () -> new RuntimeException("Customer table was not created."));
            // The actual error can be inspected in response.exception()
            System.out.println(tableDescription.table().tableName() + " was created.");
        }
    }
}