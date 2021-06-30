package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotManager;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.classes.ParticipationManager;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.classes.ProfileManager;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class PlayerReJoin extends ListenerAdapter {
    /**
     * Add Event roles to member when he come back in guild
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot() && event.getGuild().equals(BotManager.getGuild())) {
            Profile profile = ProfileManager.getProfile(event.getMember().getIdLong());
            if (profile == null) return;
            ArrayList<Participation> participationList = ParticipationManager.getParticipationList(profile.getUuid());
            for (Participation participation : participationList) {
                Role role = Main.getJDA().getRoleById(participation.getEvent().getRoleId());
                if (role==null) continue;
                BotManager.getGuild().addRoleToMember(event.getMember(), role).queue();
            }
        }
    }
}
