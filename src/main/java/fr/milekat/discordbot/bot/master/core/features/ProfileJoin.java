package fr.milekat.discordbot.bot.master.core.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.core.classes.Profile;
import fr.milekat.discordbot.bot.master.core.classes.Registration;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.core.managers.RegistrationManager;
import fr.milekat.discordbot.bot.master.moderation.classes.Ban;
import fr.milekat.discordbot.bot.master.moderation.managers.BanManager;
import fr.milekat.discordbot.bot.master.moderation.managers.MuteManager;
import fr.milekat.discordbot.utils.Config;
import fr.milekat.utils.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class ProfileJoin extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (ProfileManager.exists(event.getMember().getIdLong())) {
            /* If this member has already a profile, add his role and minecraft username */
            Main.log(event.getUser().getAsTag() + " is registered.");
            BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rProfile")).queue();
            Profile profile = ProfileManager.getProfile(event.getMember().getIdLong());
            if (BotUtils.getGuild().getSelfMember().canInteract(event.getMember())) {
                BotUtils.getGuild().modifyNickname(event.getMember(), profile.getUsername()).queue();
            }
            /* If member is ban, add user to his ban channel(s) and add role */
            if (BanManager.isBanned(profile)) {
                BotUtils.getGuild().removeRoleFromMember(event.getMember(), BotUtils.getRole("rProfile")).queue();
                BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rBan")).queue();
                Ban ban = BanManager.getLastBan(profile);
                ban.getChannel().putPermissionOverride(event.getMember()).setAllow(Permission.VIEW_CHANNEL).queue();
            }
            /* If member is muted, add mute role to user */
            if (MuteManager.isMuted(profile)) {
                BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rMute")).queue();
            }
        } else if (RegistrationManager.exists(event.getMember().getIdLong())) {
            /* If this member hasn't a profile, but start register */
            Main.log(event.getUser().getAsTag() + " is registering.");
            BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rWaiting")).queue();
            Registration registration = RegistrationManager.getRegistration(event.getMember().getIdLong());
            registration.getChannel().putPermissionOverride(event.getMember()).setAllow(Permission.VIEW_CHANNEL).queue();
        } else {
            /* else, greet him with a random message, and if is name is not AlphaNumericExtended, set him a default nickname */
            if (!Tools.isAlphaNumericExtended(event.getUser().getName()) &&
                    BotUtils.getGuild().getSelfMember().canInteract(event.getMember())) {
                ArrayList<JSONObject> renameArray = BotUtils.getNodeArray(Config.getConfig(),
                        "discord.msg.joinGuild.renameList");
                String renameName = renameArray.get(new Random().nextInt(renameArray.size())).toString();
                BotUtils.getGuild().modifyNickname(event.getMember(),
                        renameName + "#" + event.getUser().getDiscriminator()).queue();
            }
            ArrayList<JSONObject> greetingArray = BotUtils.getNodeArray(Config.getConfig(),
                    "discord.msg.joinGuild.greetings");
            String joinMsg = greetingArray.get(new Random().nextInt(greetingArray.size())).toString();
            BotUtils.getChannel("cGeneral")
                    .sendMessage(joinMsg.replaceAll("<name>", event.getMember().getAsMention()))
                    .queue();
            Main.log(event.getUser().getAsTag() + " is a new joiner");
        }
    }
}
