package fr.milekat.discordbot.bot.master.features.Moderation;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Ban;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.managers.BanManager;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Date;

public class UnBanFeature extends ListenerAdapter {
    public UnBanFeature() {
        BotUtils.getGuild().upsertCommand(
                new CommandData("unban", BotUtils.getMsg("ban.slashUnBan"))
                        .addOptions(new OptionData(OptionType.USER,
                                        "member",
                                        BotUtils.getMsg("ban.slashOptDescMember"),
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

    /**
     * /unban command
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (!event.getName().equalsIgnoreCase("unban")) return;
        if (!event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
            event.reply(BotUtils.getMsg("noPerm")).setEphemeral(true).queue();
            return;
        }
        Member targetMember = event.getOption("member").getAsMember();
        //  Check if target has a profile
        if (!ProfileManager.exists(event.getOption("member").getAsMember().getIdLong())) {
            event.reply(BotUtils.getMsg("ban.slashNoProfile")).setEphemeral(true).queue();
            return;
        }
        Profile targetProfile = ProfileManager.getProfile(targetMember.getIdLong());
        if (!BanManager.isBanned(targetProfile)) {
            event.reply(BotUtils.getMsg("ban.notBanned")).setEphemeral(true).queue();
            return;
        }
        Ban oldBan = BanManager.getLastBan(targetProfile);
        Ban updatedBan = new Ban(targetProfile, oldBan.getBanDate(), new Date(), oldBan.getReasonBan());
        BanManager.save(oldBan.setAcknowledge(true));
        BanManager.save(updatedBan.setReasonPardon(event.getOption("reason").getAsString()).setAcknowledge(true));
        BanUtils.unBanNotify(targetMember, updatedBan);
        event.reply(BotUtils.getMsg("ban.slashSuccess")).setEphemeral(true).queue();
    }

    /**
     * When member acknowledge his ban
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (!event.getButton().getId().equalsIgnoreCase(BanUtils.banAcknowledge)) return;
        Ban ban = BanManager.getBan(event.getTextChannel());
        if (ban.getProfile().getDiscordId()==event.getUser().getIdLong() && event.getMember()!=null) {
            BanUtils.unBanProcess(event.getMember(), ban);
        } else {
            event.reply("Wrong user").setEphemeral(true).queue();
        }
    }
}
