package fr.milekat.discordbot.core;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.utils.Config;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class RabbitMQ {
    private static final JSONObject RABBIT_CONFIG = (JSONObject) ((JSONObject) Config.getConfig().get("data")).get("rabbitMQ");
    private static final JSONObject CONSUMER = (JSONObject) RABBIT_CONFIG.get("consumer");

    public RabbitMQ() {
        try {
            Channel channel = getConnection().createChannel();
            channel.exchangeDeclare((String) RABBIT_CONFIG.get("exchange"), "direct");
            channel.queueDeclare((String) CONSUMER.get("queue"),
                    false, false, false, null);
            channel.queueBind((String) CONSUMER.get("queue"),
                    (String) RABBIT_CONFIG.get("exchange"),
                    (String) CONSUMER.get("routingKey"));
            channel.close();
            getConnection().close();
        } catch (IOException | TimeoutException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Init/Get RabbitMQ Connection
     */
    private static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost((String)  RABBIT_CONFIG.get("host"));
        connectionFactory.setPort(((Long) RABBIT_CONFIG.get("port")).intValue());
        connectionFactory.setUsername((String) RABBIT_CONFIG.get("user"));
        connectionFactory.setPassword((String) RABBIT_CONFIG.get("password"));
        return connectionFactory.newConnection();
    }

    /**
     * Load RABBIT_CONFIG.get("queue") Consumer
     */
    public Thread getRabbitConsumer() {
        if (Main.DEBUG_RABBIT) Main.log("Rabbit debugs enable");
        return new Thread(() -> {
            try {
                Channel channel = getConnection().createChannel();
                DeliverCallback deliverCallback = (consumerTag, message) -> {
                    if (Main.DEBUG_RABBIT) Main.log(new String(message.getBody(), StandardCharsets.UTF_8));
                    try {
                        JSONObject json = (JSONObject) new JSONParser().parse(new String(message.getBody(), StandardCharsets.UTF_8));
                        RabbitMQReceive.MessageType messageType = RabbitMQReceive.MessageType.other;
                        String type = (String) Optional.ofNullable(json.get("type")).orElse("other");
                        try {
                            messageType = RabbitMQReceive.MessageType.valueOf(type);
                        } catch (IllegalArgumentException ignore) {}
                        //  CALL HERE
                        if (Main.DEBUG_RABBIT && messageType.equals(RabbitMQReceive.MessageType.other)) {
                            Main.log("RabbitMQ Unknown type: " + type);
                        }
                        new RabbitMQReceive(messageType, json);
                    } catch (ParseException exception) {
                        exception.printStackTrace();
                    }
                };
                channel.basicConsume((String) CONSUMER.get("queue"), true, deliverCallback, Main::log);
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Send message through prod.bungee.%eventName OR "all"% queue
     */
    public static void rabbitSend(String eventName, String message) throws IOException, TimeoutException {
        Channel channel = getConnection().createChannel();
        Main.log(eventName + ":" + message);
        channel.basicPublish((String) RABBIT_CONFIG.get("exchange"), "prod.bungee." + eventName, null,
                message.getBytes(StandardCharsets.UTF_8));
        channel.close();
        getConnection().close();
    }
}
