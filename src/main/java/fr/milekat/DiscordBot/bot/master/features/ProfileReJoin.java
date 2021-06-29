package fr.milekat.DiscordBot.bot.master.features;

import fr.milekat.DiscordBot.bot.master.classes.Profile;
import fr.milekat.DiscordBot.bot.master.classes.ProfileManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

public class ProfileReJoin extends ListenerAdapter {
    /* Guilds */
    private final Guild gPublic;
    /* Roles */
    private final Role rValid;

    public ProfileReJoin(JDA jda, JSONObject id) {
        this.gPublic = jda.getGuildById((Long) id.get("gPublic"));
        this.rValid = jda.getRoleById((Long) id.get("rValid"));
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot() && event.getGuild().equals(gPublic)) {
            Profile profile = ProfileManager.getProfile(event.getMember().getIdLong());
            if (profile == null) return;
            gPublic.addRoleToMember(event.getMember(), rValid).queue();
            if (gPublic.getSelfMember().canInteract(event.getMember())) {
                gPublic.modifyNickname(event.getMember(), profile.getName()).queue();
            }
        }
    }
}
