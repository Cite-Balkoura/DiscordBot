package fr.milekat.discordbot.bot.events;

import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.classes.ParticipationManager;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.classes.ProfileManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class PlayerReJoin extends ListenerAdapter {
    /* */
    private final JDA jda;
    /* Guilds */
    private final Guild gPublic;

    public PlayerReJoin(JDA jda, JSONObject id) {
        this.jda = jda;
        this.gPublic = jda.getGuildById((Long) id.get("gPublic"));
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot() && event.getGuild().equals(gPublic)) {
            Profile profile = ProfileManager.getProfile(event.getMember().getIdLong());
            if (profile == null) return;
            ArrayList<Participation> participationList = ParticipationManager.getParticipationList(profile.getUuid());
            for (Participation participation : participationList) {
                Role role = jda.getRoleById(participation.getEvent().getRoleId());
                if (role==null) continue;
                gPublic.addRoleToMember(event.getMember(), role).queue();
            }
        }
    }
}
