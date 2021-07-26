package fr.milekat.discordbot.core;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import fr.milekat.discordbot.Main;
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
import java.util.Collections;
import java.util.HashMap;

public class Init {
    /**
     * Load the config file (config.json)
     */
    public JSONObject getConfigs() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject configs = (JSONObject) jsonParser.parse(new FileReader("config.json"));
        Main.DEBUG_ERROR = (boolean) ((JSONObject) configs.get("config")).get("debugError");
        Main.DEBUG_RABBIT = (boolean) ((JSONObject) configs.get("config")).get("debugRabbitMQ");
        Main.MODE_DEV = (boolean) ((JSONObject) configs.get("config")).get("devMode");
        return configs;
    }

    /**
     * Load DataStores
     */
    public HashMap<String, Datastore> getDatastoreMap() {
        HashMap<String, Datastore> datastoreMap = new HashMap<>();
        for (Object dbName : ((JSONArray) ((JSONObject) ((JSONObject) Main.getConfig().get("data")).get("mongo")).get("databases"))) {
            if (Main.DEBUG_ERROR) Main.log("[Mongo] Load db: " + dbName.toString());
            datastoreMap.put(dbName.toString(), setDatastore(dbName.toString()));
        }
        if (Main.DEBUG_ERROR) Main.log("[Mongo] " + datastoreMap.size() + " db loaded");
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
                ((String) mongoConfig.get("password")).toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress((String) mongoConfig.get("host"), ((Long) mongoConfig.get("port")).intValue()))))
                .credential(credential)
                .build();
        Datastore datastore = Morphia.createDatastore(MongoClients.create(settings), dbName, MapperOptions.builder()
                .enablePolymorphicQueries(true)
                .build());
        datastore.getMapper().mapPackage("fr.milekat.discordbot.bot.events.classes");
        datastore.getMapper().mapPackage("fr.milekat.discordbot.bot.master.classes");
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
        JDA api = JDABuilder.createDefault((String) ((JSONObject) Main.getConfig().get("discord")).get("botToken"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).build().awaitReady();
        api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching((String) ((JSONObject) Main.getConfig().get("discord")).get("botGame")));
        return api;
    }
}
