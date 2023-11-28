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
    private static final String ALBUM_TABLE_NAME = "Albums";
    private static final String LIKES_TABLE_NAME = "AlbumLikesDislikes";
    private static final String PRIMARY_KEY = "albumID";
    private static DynamoDbClient dynamoDbClient;

    // Call this method before using the DynamoDbTableManager
    public static void initializeDbManager() {
        AwsSessionCredentials awsCreds = AwsSessionCredentials.create(
                "ASIA5NCUJV5C7OZWFYNC",
                "bTrANI0SxRTQ+Xp7nl3bcT0Mj2tRwaladJScHCh4",
                "FwoGZXIvYXdzEG4aDD46iV3xTMk7UY+YoSLLAcK2tdV0X4BQHiYGKPF3w/Nj1QtAfBglp1GQv79eSSPFPJFwj5/kOx/Rxvsv0UoIublM4QIs13BgA+1YtPP/eqLu+bpc/p04L9baRKLPnifnL2R1XE+1AzdIP4kYwXQxjvnmXi3s884rx8oNjiGXebkykLqKRSfDKTdD/ddH6b+PFCf1W56pOeIAaqciosDSeUeferq778RYSEuv5n3JC2BRqIRCuVy+W+d/u0swKRqh9XKA3ZdQhrTYJVKw5L/p4V6oCPGzbx9TRoe/KMTZlasGMi2cXj4APB786FXDh8YOqzR0VA2ezynk12Yrui/GWF1I2G8aaITODHq/h5R5plw="
        );
        // You would typically set the region to the region where your DynamoDB table is hosted
        dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.US_WEST_2)
                .build();

        initializeDbTable();
    }

    public static void initializeDbTable() {
        try {
            DescribeTableResponse describeTableResponse = dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(ALBUM_TABLE_NAME)
                    .build());

            // If we get here, it means the table exists
            System.out.println("Table already exists: " + ALBUM_TABLE_NAME);
        } catch (DynamoDbException e) {
            // The table doesn't exist, create it
            System.out.println("Table doesn't exists, creating table: " + ALBUM_TABLE_NAME);
            createTable(ALBUM_TABLE_NAME);
        }

        try {
            DescribeTableResponse describeTableResponse = dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(LIKES_TABLE_NAME)
                    .build());

            // If we get here, it means the table exists
            System.out.println("Table already exists: " + LIKES_TABLE_NAME);
        } catch (DynamoDbException e) {
            // The table doesn't exist, create it
            System.out.println("Table doesn't exists, creating table: " + LIKES_TABLE_NAME);
            createTable(LIKES_TABLE_NAME);
        }
    }

    private static void createTable(String tableName) {
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
                    .tableName(tableName)
                    .build();

            dynamoDbClient.createTable(request);

            System.out.println("Table created successfully with on-demand capacity: " + tableName);
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

        putItem(item,ALBUM_TABLE_NAME);

        Map<String, AttributeValue> likeItem = new HashMap<>();
        likeItem.put("albumID", AttributeValue.builder().s(album.getId()).build());
        likeItem.put("likeCount", AttributeValue.builder().n("0").build());
        likeItem.put("dislikeCount", AttributeValue.builder().n("0").build());

        putItem(likeItem,LIKES_TABLE_NAME);
    }

    public static void putItem(Map<String, AttributeValue> item, String tableName) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        try {
            PutItemResponse response = dynamoDbClient.putItem(request);

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
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
                .tableName(ALBUM_TABLE_NAME)
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

    public static void updateLikeDislike(String albumId, String action) {
        UpdateItemRequest request = buildUpdateRequest(albumId, action);
        try {
            dynamoDbClient.updateItem(request);
        } catch (DynamoDbException e) {
            System.err.println("Unable to update like/dislike count: ");
            System.err.println(e.getMessage());
            throw e;
        }
    }

    private static UpdateItemRequest buildUpdateRequest(String albumId, String action) {
        String updateExpression;
        if ("like".equals(action)) {
            updateExpression = "SET likeCount = if_not_exists(likeCount, :start) + :inc";
        } else if ("dislike".equals(action)) {
            updateExpression = "SET dislikeCount = if_not_exists(dislikeCount, :start) + :inc";
        } else {
            throw new IllegalArgumentException("Invalid action: " + action);
        }

        return UpdateItemRequest.builder()
                .tableName(LIKES_TABLE_NAME)
                .key(Map.of("albumID", AttributeValue.builder().s(albumId).build()))
                .updateExpression(updateExpression)
                .expressionAttributeValues(Map.of(
                        ":inc", AttributeValue.builder().n("1").build(),
                        ":start", AttributeValue.builder().n("0").build()
                ))
                .build();
    }
}

