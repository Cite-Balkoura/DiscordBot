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
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.classes.Step;
import fr.milekat.discordbot.bot.master.core.classes.StepInput;
import fr.milekat.discordbot.bot.master.moderation.classes.Ban;
import fr.milekat.discordbot.bot.master.moderation.classes.Mute;
import fr.milekat.discordbot.utils.Config;
import org.bson.UuidRepresentation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;

public class MongoDB {

    /**
     * Load DataStores
     */
    public static HashMap<String, Datastore> getDatastoreMap() {
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
    private static Datastore setDatastore(String dbName) {
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
        if (dbName.equalsIgnoreCase("master")) {
            datastore.getMapper().map(Event.class);
            datastore.getMapper().map(Profile.class, Participation.class, Step.class, StepInput.class);
            datastore.getMapper().map(Ban.class, Mute.class);
        } else {
            datastore.getMapper().map(Team.class);
        }
        datastore.ensureIndexes();
        datastore.ensureCaps();
        datastore.enableDocumentValidation();
        return datastore;
    }


}
