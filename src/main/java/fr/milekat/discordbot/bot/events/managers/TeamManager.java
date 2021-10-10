package fr.milekat.discordbot.bot.events.managers;

import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.events.classes.Event;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import org.bson.types.ObjectId;

import java.util.Collections;

public class TeamManager {
    /**
     * Get a Team from a member
     */
    public static Team getTeam(Event event, ObjectId id) {
        return Main.getDatastore(event.getDatabase()).find(Team.class)
                .filter(Filters.eq("_id", id))
                .first();
    }

    /**
     * Get Team of profile in event
     */
    public static Team getTeam(Event event, Profile profile) {
        return Main.getDatastore(event.getDatabase()).find(Team.class)
                .filter(Filters.eq("eventName", event.getName()))
                .filter(Filters.in("members", Collections.singletonList(profile.getUuid())))
                .first();
    }

    /**
     * Get a Team by his private channelId
     */
    public static Team getTeam(Event event, long channelId) {
        return Main.getDatastore(event.getDatabase()).find(Team.class)
                .filter(Filters.eq("eventName", event.getName()))
                .filter(Filters.eq("channelId", channelId))
                .first();
    }

    /**
     * Get a Team by his presentation message
     */
    public static Team getTeamByMsg(Event event, long messageId) {
        return Main.getDatastore(event.getDatabase()).find(Team.class)
                .filter(Filters.eq("eventName", event.getName()))
                .filter(Filters.eq("messageId", messageId))
                .first();
    }

    /**
     * Check if the profile has a team on this event
     */
    public static boolean exists(Event event, Profile profile) {
        return Main.getDatastore(event.getDatabase()).find(Team.class)
                .filter(Filters.eq("eventName", event.getName()))
                .filter(Filters.in("members", Collections.singletonList(profile.getUuid())))
                .first()!=null;
    }

    /**
     * Check if team exist
     */
    public static boolean exists(Event event, String teamName) {
        return Main.getDatastore(event.getDatabase()).find(Team.class)
                .filter(Filters.eq("eventName", event.getName()))
                .filter(Filters.eq("teamName", teamName))
                .first()!=null;
    }

    /**
     * Update teamName of a team
     */
    public static void updateName(Team team) {
        Main.getDatastore(team.getEvent().getDatabase()).find(Team.class)
                .filter(Filters.eq("_id", team.getId()))
                .update(UpdateOperators.set("teamName", team.getTeamName()))
                .execute();
    }

    /**
     * Update message of a team
     */
    public static void updateMessageId(Team team) {
        Main.getDatastore(team.getEvent().getDatabase()).find(Team.class)
                .filter(Filters.eq("_id", team.getId()))
                .update(UpdateOperators.set("messageId", team.getMessageId()))
                .execute();
    }

    /**
     * Update channel of a team
     */
    public static void updateChannelId(Team team) {
        Main.getDatastore(team.getEvent().getDatabase()).find(Team.class)
                .filter(Filters.eq("_id", team.getId()))
                .update(UpdateOperators.set("channelId", team.getChannelId()))
                .execute();
    }

    /**
     * Update open state of a team
     */
    public static void updateAccess(Team team) {
        Main.getDatastore(team.getEvent().getDatabase()).find(Team.class)
                .filter(Filters.eq("_id", team.getId()))
                .update(UpdateOperators.set("access", team.isOpen()))
                .execute();
    }

    /**
     * Update open state of a team
     */
    public static void updateDescription(Team team) {
        Main.getDatastore(team.getEvent().getDatabase()).find(Team.class)
                .filter(Filters.eq("_id", team.getId()))
                .update(UpdateOperators.set("description", team.getDescription()))
                .execute();
    }

    /**
     * Update open state of a team
     */
    public static void updateMembers(Team team) {
        Main.getDatastore(team.getEvent().getDatabase()).find(Team.class)
                .filter(Filters.eq("_id", team.getId()))
                .update(UpdateOperators.set("members", team.getMembers()))
                .execute();
    }

    /**
     * Save a team
     */
    public static void create(Team team) {
        Main.getDatastore(team.getEvent().getDatabase()).save(team);
    }
}
