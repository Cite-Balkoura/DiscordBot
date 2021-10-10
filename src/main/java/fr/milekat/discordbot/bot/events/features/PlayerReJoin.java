package fr.milekat.discordbot.bot.events.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.events.classes.Participation;
import fr.milekat.discordbot.bot.events.classes.Team;
import fr.milekat.discordbot.bot.events.managers.ParticipationManager;
import fr.milekat.discordbot.bot.events.managers.TeamManager;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import net.dv8tion.jda.api.Permission;
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
            ArrayList<Participation> participationList = ParticipationManager.getParticipationList(profile);
            for (Participation participation : participationList) {
                Role role = Main.getJDA().getRoleById(participation.getEvent().getRoleId());
                if (role==null) continue;
                BotUtils.getGuild().addRoleToMember(event.getMember(), role).queue();
                if (TeamManager.exists(participation.getEvent(), profile)) {
                    Team team = TeamManager.getTeam(participation.getEvent(), profile);
                    if (team.getChannel()!=null) BotUtils.getGuild().retrieveMemberById(profile.getDiscordId()).queue(member ->
                            team.getChannel().putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue()
                    );
                }
            }
        }
    }
}
