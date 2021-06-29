package fr.milekat.DiscordBot.core;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import fr.milekat.DiscordBot.Main;
import fr.milekat.DiscordBot.utils.MariaManage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Init {
    /**
     * Load the config file (config.json)
     */
    public JSONObject getConfigs() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject configs = (JSONObject) jsonParser.parse(new FileReader("config.json"));
        Main.DEBUG_ERROR = (boolean) configs.get("debug");
        Main.DEBUG_JEDIS = (boolean) configs.get("debugjedis");
        Main.MODE_DEV = (boolean) configs.get("devmode");
        return configs;
    }

    /**
     * SQL connection + SQL auto ping to prevent the connection to get disconnected
     */
    public MariaManage setSQL() {
        JSONObject sqlConfig = (JSONObject) Main.getConfig().get("sql");
        //  Open SQL connection
        MariaManage mariaManage = new MariaManage("jdbc:mysql://",
                (String) sqlConfig.get("host"),
                (String) sqlConfig.get("db"),
                (String) sqlConfig.get("user"),
                (String) sqlConfig.get("mdp"));
        mariaManage.connection();
        //  Start SQL ping to keep alive SQL connection
        new Thread("SQL-keepalive") {
            @Override
            public void run() {
                Timer SQL_keepalive = new Timer();
                SQL_keepalive.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            PreparedStatement q = mariaManage.getConnection().prepareStatement("SELECT * FROM `ping`;");
                            q.execute();
                            q.close();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    }
                }, 0, 600000);
            }
        }.start();
        return mariaManage;
    }

    public HashMap<String, Datastore> getDatastoreMap() {
        HashMap<String, Datastore> datastoreMap = new HashMap<>();
        for (Object dbName : ((JSONArray) ((JSONObject) ((JSONObject) Main.getConfig().get("data")).get("mongo")).get("databases"))) {
            datastoreMap.put(dbName.toString(), setDatastore(dbName.toString()));
        }
        return datastoreMap;
    }

    /**
     * MongoDB Connection (Morphia Datastore) to query
     */
    private Datastore setDatastore(String dbName) {
        JSONObject mongoConfig = (JSONObject) ((JSONObject) Main.getConfig().get("data")).get("mongo");
        MongoCredential credential = MongoCredential.createCredential(
                (String) mongoConfig.get("user"),
                (String) mongoConfig.get("db"),
                ((String) mongoConfig.get("mdp")).toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress())))
                .build();
        Datastore datastore = Morphia.createDatastore(MongoClients.create(settings), dbName, MapperOptions.builder()
                .enablePolymorphicQueries(true)
                .build());
        datastore.getMapper().mapPackage("");
        datastore.ensureIndexes();
        datastore.ensureCaps();
        datastore.enableDocumentValidation();
        return datastore;
    }

    /**
     * Load console thread
     */
    public Thread getConsole() {
        return new Thread("Console") {
            @Override
            public void run() {
                new Console();
            }
        };
    }

    /**
     * Connect to the Discord bot and set the watching text
     */
    public JDA getJDA() throws LoginException, InterruptedException {
        JDA api = JDABuilder.createDefault((String) Main.getConfig().get("bot token"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS).disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).build().awaitReady();
        api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching((String) Main.getConfig().get("bot_game")));
        return api;
    }

    /**
     * Get all channels to subscribe with SQL list
     */
    private String[] getJedisChannels() {
        try {
            Connection connection = Main.getSql();
            PreparedStatement q = connection.prepareStatement("SELECT * FROM `mcpg_redis_channels`");
            q.execute();
            ArrayList<String> jedisChannels = new ArrayList<>();
            while (q.getResultSet().next()) { jedisChannels.add(q.getResultSet().getString("channel")); }
            q.close();
            return jedisChannels.toArray(new String[0]);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
