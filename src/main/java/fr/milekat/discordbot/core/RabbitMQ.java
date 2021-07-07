package fr.milekat.discordbot.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.milekat.discordbot.Main;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {
    private static Connection CONNECTION = null;
    private static final JSONObject RABBIT_CONFIG = (JSONObject) ((JSONObject) Main.getConfig().get("data")).get("rabbitMQ");

    /**
     * Init/Get RabbitMQ Connection
     */
    private static Connection getConnection() {
        if (CONNECTION == null) {
            try {
                ConnectionFactory connectionFactory = new ConnectionFactory();
                connectionFactory.setHost((String)  RABBIT_CONFIG.get("host"));
                connectionFactory.setPort(((Long) RABBIT_CONFIG.get("port")).intValue());
                connectionFactory.setUsername((String) RABBIT_CONFIG.get("user"));
                connectionFactory.setPassword((String) RABBIT_CONFIG.get("password"));
                CONNECTION = connectionFactory.newConnection();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
        return CONNECTION;
    }

    /**
     * Load RABBIT_CONFIG.get("queue") Consumer
     */
    public Thread getRabbitConsumer() throws IOException {
        Channel channel = getConnection().createChannel();
        DeliverCallback deliverCallback = (consumerTag, message) -> Main.log(new String(message.getBody(), StandardCharsets.UTF_8));
        return new Thread(() -> {
                    try {
                        channel.basicConsume((String) RABBIT_CONFIG.get("queue"), true, deliverCallback, Main::log);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, "RabbitConsumer");
    }

    /**
     * Send message through RABBIT_CONFIG.get("routingKey") queue
     */
    public static void rabbitSend(String message) throws IOException, TimeoutException {
        Channel channel = getConnection().createChannel();
        channel.basicPublish((String) RABBIT_CONFIG.get("exchange"), (String) RABBIT_CONFIG.get("routingKey"), null, message.getBytes(StandardCharsets.UTF_8));
        channel.close();
    }
}
