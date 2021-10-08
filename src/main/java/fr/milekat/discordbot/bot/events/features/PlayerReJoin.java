package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.managers.ParticipationManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
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
        if (!event.getUser().isBot() && event.getGuild().equals(BotUtils.getGuild())) {
            Profile profile = ProfileManager.getProfile(event.getMember().getIdLong());
            if (profile == null) return;
            ArrayList<Participation> participationList = ParticipationManager.getParticipationList(profile.getUuid());
            for (Participation participation : participationList) {
                Role role = Main.getJDA().getRoleById(participation.getEvent().getRoleId());
                if (role==null) continue;
                BotUtils.getGuild().addRoleToMember(event.getMember(), role).queue();
            }
            // TODO: 08/10/2021 Team(s) rejoin
        }
    }
}
