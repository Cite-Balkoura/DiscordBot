package fr.milekat.discordbot.core;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.master.Moderation.classes.Ban;
import fr.milekat.discordbot.bot.master.Moderation.classes.Mute;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.classes.Step;
import fr.milekat.discordbot.bot.master.core.classes.StepInput;
import fr.milekat.discordbot.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bson.UuidRepresentation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

public class Init {
    public Init() throws IOException, ParseException {
        Config.loadConfig();
    }

    /**
     * Load DataStores
     */
    public HashMap<String, Datastore> getDatastoreMap() {
        HashMap<String, Datastore> datastoreMap = new HashMap<>();
        for (Object dbName : ((JSONArray) ((JSONObject) ((JSONObject) Config.getConfig().get("data")).get("mongo")).get("databases"))) {
            if (Main.DEBUG_ERRORS) Main.log("[Mongo] Load db: " + dbName.toString());
            datastoreMap.put(dbName.toString(), setDatastore(dbName.toString()));
        }
        if (Main.DEBUG_ERRORS) Main.log("[Mongo] " + datastoreMap.size() + " db loaded");
        return datastoreMap;
    }

    /**
     * MongoDB Connection (Morphia Datastore) to query
     */
    private Datastore setDatastore(String dbName) {
        JSONObject mongoConfig = (JSONObject) ((JSONObject) Config.getConfig().get("data")).get("mongo");
        MongoCredential credential = MongoCredential.createCredential(
                (String) mongoConfig.get("user"),
                (String) mongoConfig.get("db"),
                ((String) mongoConfig.get("password")).toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .uuidRepresentation(UuidRepresentation.JAVA_LEGACY)
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(new ServerAddress((String) mongoConfig.get("host"), ((Long) mongoConfig.get("port")).intValue()))))
                .credential(credential)
                .build();
        Datastore datastore = Morphia.createDatastore(MongoClients.create(settings), dbName, MapperOptions.builder()
                .enablePolymorphicQueries(true)
                .build());
        datastore.getMapper().map(Event.class, Participation.class, Team.class);
        datastore.getMapper().map(Profile.class, Step.class, StepInput.class);
        datastore.getMapper().map(Ban.class, Mute.class);
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
        JDA api = JDABuilder.createDefault((String) ((JSONObject) Config.getConfig().get("discord")).get("botToken"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE).build().awaitReady();
        api.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching((String) ((JSONObject) Config.getConfig().get("discord")).get("botGame")));
        return api;
    }
}
