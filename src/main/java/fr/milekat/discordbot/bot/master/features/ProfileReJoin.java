package fr.milekat.discordbot.bot.master.features;

import fr.milekat.discordbot.Main;
import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Ban;
import fr.milekat.discordbot.bot.master.classes.Registration;
import fr.milekat.discordbot.bot.master.managers.BanManager;
import fr.milekat.discordbot.bot.master.managers.MuteManager;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.managers.RegistrationManager;
import fr.milekat.discordbot.utils.Config;
import fr.milekat.discordbot.utils.Tools;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Random;

public class ProfileReJoin extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot() && event.getGuild().equals(BotUtils.getGuild())) {
            if (ProfileManager.exists(event.getMember().getIdLong())) {
                /* If this member has already a profile, add his role and minecraft username */
                Main.log(event.getUser().getAsTag() + " is registered.");
                BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rProfile")).queue();
                if (BotUtils.getGuild().getSelfMember().canInteract(event.getMember())) {
                    BotUtils.getGuild().modifyNickname(event.getMember(),
                            ProfileManager.getProfile(event.getMember().getIdLong()).getUsername()).queue();
                }
                if (BanManager.isBanned(event.getMember().getIdLong())) {
                    BotUtils.getGuild().addRoleToMember(event.getMember(), BotUtils.getRole("rBan")).queue();
                    ArrayList<Ban> bans = BanManager.getCurrentBans(event.getMember().getIdLong());
                    bans.forEach(ban -> ban.getChannel().putPermissionOverride(event.getMember()).setAllow(Permission.VIEW_CHANNEL).queue());
                }
                if (MuteManager.isMuted(event.getMember().getIdLong())) {
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
                    JSONArray renameArray = BotUtils.getNodeArray(Config.getConfig(), "discord.msg.joinGuild.renameList");
                    String renameName = renameArray.get(new Random().nextInt(renameArray.size())).toString();
                    BotUtils.getGuild().modifyNickname(event.getMember(),
                            renameName + "#" + event.getUser().getDiscriminator()).queue();
                }
                JSONArray greetingArray = BotUtils.getNodeArray(Config.getConfig(), "discord.msg.joinGuild.greetings");
                String joinMsg = greetingArray.get(new Random().nextInt(greetingArray.size())).toString();
                BotUtils.getChannel("cGeneral")
                        .sendMessage(joinMsg.replaceAll("<name>", event.getMember().getAsMention()))
                        .queue();
            }
        }
    }
}
