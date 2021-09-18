package fr.milekat.discordbot.bot.master.Moderation.commands;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.Moderation.ModerationUtils;
import fr.milekat.discordbot.bot.master.Moderation.classes.Ban;
import fr.milekat.discordbot.bot.master.Moderation.managers.BanManager;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;

public class UnBanCmd extends ListenerAdapter {
    public UnBanCmd() {
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
        if (!event.getName().equalsIgnoreCase("unban")) return;
        if (ModerationUtils.cantProcess(event)) return;
        ModerationUtils.unBanSend(event.getOption("member").getAsMember(),
                ProfileManager.getProfile(event.getOption("member").getAsMember().getIdLong()),
                event.getOption("reason").getAsString());
        event.reply(BotUtils.getMsg("ban.slashSuccess")).setEphemeral(true).queue();
    }

    /**
     * When member acknowledge his ban
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (!event.getButton().getId().equalsIgnoreCase(ModerationUtils.banAcknowledge)) return;
        Ban ban = BanManager.getBan(event.getTextChannel());
        if (ban.getProfile().getDiscordId()==event.getUser().getIdLong() && event.getMember()!=null) {
            ModerationUtils.unBanProcess(event.getMember(), ban);
        } else {
            event.reply("Wrong user").setEphemeral(true).queue();
        }
    }
}