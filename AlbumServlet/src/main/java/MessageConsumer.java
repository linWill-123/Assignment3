import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import db.DynamoDbTableManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageConsumer implements Runnable {
    private final String QUEUE_NAME = "REVIEWS_QUEUE";
    private final String EXCHANGE_NAME = "REVIEWS_EXCHANGE";
    private final Connection connection;


    public MessageConsumer(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        try {
            boolean autoAck = true;
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME,"direct");
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");

//            channel.basicQos(1);

            System.out.println(" [*] Waiting for messages.");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                String message = new String(delivery.getBody(), "UTF-8");
                processMessage(message);
            };

            channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, consumerTag -> { });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String message) {
        // Split message into albumId, userId, and action
        String[] parts = message.split(",");
        if (parts.length == 2) {
            String albumId = parts[0];
            String action = parts[1];

            // Update DynamoDB
            DynamoDbTableManager.updateLikeDislike(albumId, action);
        } else {
            System.err.println("Invalid message format: " + message);
        }
    }
}
