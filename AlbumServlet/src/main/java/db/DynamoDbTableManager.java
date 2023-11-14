package db;

import model.Album;
import model.Profile;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.awssdk.regions.Region;


import java.util.HashMap;
import java.util.Map;

public class DynamoDbTableManager {
    private static final String TABLE_NAME = "Albums";
    private static final String PRIMARY_KEY = "albumID";
    private static DynamoDbClient dynamoDbClient;



    // Call this method before using the DynamoDbTableManager
    private static void initializeDbClient() {
        AwsSessionCredentials awsCreds = AwsSessionCredentials.create(
                "ASIA5NCUJV5CXS7YGIUD",
                "73lxGxnGLlK+W9oaC0A4gIPO9SLU+gBqUv9j5ewJ",
                "FwoGZXIvYXdzEJv//////////wEaDMqNO/FgeOsPGP9Y5SLLAVtkCxw0iIehpy5r7FN82eBZJsXzYlYA6Zznjr2KfPgbRGb6RcVJbw1CdmMsYpG6u36J8x8x8xxSNPGHC3Yg1kwHuO4T2fRsD7WuKo8CljhVLaOSkAjXM7aHnr2SZtP2D40o1sxR9aij2u6MT+ym3Utf4VE9XVoLQwUa/L7OHwsMhUjI60rOz3w2FjmoiWRu+65nvhsTei6rJTFEMqlHq/tzf915ai4GZBvbqi3+YFeQ/u4/Xa5JNaWThMASSihRWaTl8tdt8+JjAHQIKLuHr6oGMi3T6pAkD1C3Mu2+DhFb7FLHaT05CG2vplB8bCE3pUyBja1PP+ZvLUrP4vBjrFk="
        );
        // You would typically set the region to the region where your DynamoDB table is hosted
        dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.US_WEST_2)
                .build();
    }
    public static void initializeDbTable() {
        initializeDbClient();

        try {
            DescribeTableResponse describeTableResponse = dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .build());

            // If we get here, it means the table exists
            System.out.println("Table already exists: " + TABLE_NAME);
        } catch (DynamoDbException e) {
            // The table doesn't exist, create it
            System.out.println("Table doesn't exists, creating table: " + TABLE_NAME);
            createTable();
        }
    }

    public static void createTable() {
        try {
            CreateTableRequest request = CreateTableRequest.builder()
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName(PRIMARY_KEY)
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .keySchema(KeySchemaElement.builder()
                            .attributeName(PRIMARY_KEY)
                            .keyType(KeyType.HASH) // Partition key
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST) // Set billing mode to on-demand
                    .tableName(TABLE_NAME)
                    .build();

            dynamoDbClient.createTable(request);

            System.out.println("Table created successfully with on-demand capacity: " + TABLE_NAME);
        } catch (DynamoDbException e) {
            System.err.println("Table creation failed: " + e.getMessage());
            throw e;
        }
    }


    public static void putAlbum(Album album) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("albumID", AttributeValue.builder().s(album.getId()).build());
        item.put("artist", AttributeValue.builder().s(album.getProfile().getArtist()).build());
        item.put("title", AttributeValue.builder().s(album.getProfile().getTitle()).build());
        item.put("year", AttributeValue.builder().s(album.getProfile().getYear()).build());


        if (album.getImage() != null && album.getImage().length > 0) {
            item.put("image", AttributeValue.builder().b(SdkBytes.fromByteArray(album.getImage())).build());
        }

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(item)
                .build();

        try {
            PutItemResponse response = dynamoDbClient.putItem(request);

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", TABLE_NAME);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            throw e;

        } catch (DynamoDbException e) {
            System.err.println("Unable to add item: ");
            System.err.println(e.getMessage());
            throw e;
        }
    }

    public static Album getAlbum(String albumId) {
        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(Map.of("albumID", AttributeValue.builder().s(albumId).build()))
                .build();

        try {
            GetItemResponse response = dynamoDbClient.getItem(request);

            if (response.item() != null && !response.item().isEmpty()) {
                Map<String, AttributeValue> item = response.item();

                // Assuming that 'artist', 'title', and 'year' are the attribute names in the DynamoDB
                Profile profile = new Profile(
                        item.get("artist").s(),
                        item.get("title").s(),
                        item.get("year").s()
                );

                byte[] imageData = null;
                if (item.containsKey("image") && item.get("image").b() != null) {
                    SdkBytes imageBytes = item.get("image").b();
                    imageData = imageBytes.asByteArray();
                }

                return new Album(albumId, profile, imageData);

            } else {
                // No item with the given albumID exists
                return null;
            }
        } catch (DynamoDbException e) {
            System.err.println("Unable to get item: ");
            System.err.println(e.getMessage());
            throw e;
        }
    }
}

