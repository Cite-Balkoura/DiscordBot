package fr.milekat.discordbot.bot.master.features.Moderation;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Ban;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.managers.BanManager;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import fr.milekat.discordbot.utils.DateMileKat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Date;

public class BanCommand extends ListenerAdapter {
    public BanCommand() {
        BotUtils.getGuild().upsertCommand(new CommandData("ban", BotUtils.getMsg("ban.slashBan"))
                .addOptions(new OptionData(OptionType.USER,
                                "member",
                                BotUtils.getMsg("ban.slashOptDescMember"),
                                true),
                        new OptionData(OptionType.STRING,
                                "duration",
                                BotUtils.getMsg("ban.slashOptDescDuration"),
                                true),
                        new OptionData(OptionType.STRING,
                                "reason",
                                BotUtils.getMsg("ban.slashOptDescReason"),
                                true)
                ).setDefaultEnabled(false)
        ).queue(command -> BotUtils.getGuild().updateCommandPrivilegesById(command.getIdLong(),
                new CommandPrivilege(CommandPrivilege.Type.ROLE, true, BotUtils.getRole("rAdmin").getIdLong())
        ).queue());
    }

    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (!event.getName().equalsIgnoreCase("ban")) return;
        if (!event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
            event.reply(BotUtils.getMsg("noPerm")).setEphemeral(true).queue();
            return;
        }
        Member targetMember = event.getOption("member").getAsMember();
        //  Check if target has a profile
        if (!ProfileManager.exists(targetMember.getIdLong())) {
            event.reply(BotUtils.getMsg("ban.slashNoProfile")).setEphemeral(true).queue();
            BotUtils.getGuild().addRoleToMember(targetMember, BotUtils.getRole("rMute")).queue();
            return;
        }
        long banDelay = DateMileKat.parsePeriod(event.getOption("duration").getAsString()) + new Date().getTime();
        //  Check if mute less than 10s (10000ms)
        if (banDelay < (new Date().getTime()+10000)){
            event.reply(BotUtils.getMsg("ban.slashDelayToLow")).setEphemeral(true).queue();
            return;
        }
        String reason = event.getOption("reason").getAsString();
        BotUtils.getGuild().addRoleToMember(targetMember, BotUtils.getRole("rBan")).queue();
        Profile targetProfile = ProfileManager.getProfile(targetMember.getIdLong());
        if (BanManager.isBanned(targetProfile)) {
            Ban oldBan = BanManager.getLastBan(targetProfile);
            Ban newBan = new Ban(oldBan.getProfile(), oldBan.getBanDate(), new Date(banDelay), reason);
            if (oldBan.getChannel()!=null && !BotUtils.getGuild().getTextChannels().contains(oldBan.getChannel())) {
                sendBan(targetMember, newBan);
            } else {
                newBan.setChannel(oldBan.getChannel());
                oldBan.getChannel().sendMessage(BotUtils.getMsg("ban.informUpdate")
                        .replaceAll("<mention>", targetMember.getAsMention())
                        .replaceAll("<reason>", newBan.getReasonBan())
                        .replaceAll("<ban-delay>", DateMileKat.reamingToString(newBan.getPardonDate()))
                        .replaceAll("<pardon-date>", DateMileKat.getDate(newBan.getPardonDate()))
                ).queue();
            }
            BanManager.save(newBan);
        } else {
            Ban newBan = new Ban(targetProfile, new Date(), new Date(banDelay), reason);
            sendBan(targetMember, newBan);
        }
        event.reply(BotUtils.getMsg("ban.slashSuccess")).setEphemeral(true).queue();
    }

    /**
     * Create channel, update
     */
    public void sendBan(@Nonnull Member member, Ban ban) {
        BotUtils.getGuild().createTextChannel("ban-" + member.getEffectiveName(), BotUtils.getCategory("cBan"))
                .queue(textChannel -> {
                    BanManager.save(BanManager.getLastBan(ban.getProfile()).setChannel(textChannel));
                    textChannel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue();
                    textChannel.sendMessage(BotUtils.getMsg("ban.informMessage")
                            .replaceAll("<mention>", member.getAsMention())
                            .replaceAll("<reason>", ban.getReasonBan())
                            .replaceAll("<ban-delay>", DateMileKat.reamingToString(ban.getPardonDate()))
                            .replaceAll("<pardon-date>", DateMileKat.getDate(ban.getPardonDate()))
                    ).queue();
                });
    }
}
