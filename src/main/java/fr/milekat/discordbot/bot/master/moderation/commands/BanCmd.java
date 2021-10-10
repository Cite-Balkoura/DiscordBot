package fr.milekat.discordbot.bot.master.moderation.commands;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.moderation.ModerationUtils;
import fr.milekat.utils.DateMileKat;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Date;

public class BanCmd extends ListenerAdapter {
    public BanCmd() {
        BotUtils.getGuild().upsertCommand(BotUtils.getCommand("ban.banCmd").setDefaultEnabled(false)
        ).queue(command -> BotUtils.getGuild().updateCommandPrivilegesById(command.getIdLong(),
                new CommandPrivilege(CommandPrivilege.Type.ROLE, true, BotUtils.getRole("rAdmin").getIdLong())
        ).queue());
    }

    /**
     * /ban command
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("ban")) return;
        if (ModerationUtils.cantProcessDelay(event)) return;
        ModerationUtils.banSend(event.getOption("member").getAsMember(),
                ProfileManager.getProfile(event.getOption("member").getAsMember().getIdLong()),
                ProfileManager.getProfile(event.getUser().getIdLong()),
                DateMileKat.parsePeriod(event.getOption("duration").getAsString()) + new Date().getTime(),
                event.getOption("reason").getAsString());
        event.reply(BotUtils.getMsg("ban.slashSuccess")).setEphemeral(true).queue();
    }

    /**
     * Log ban channels messages
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getGuild().equals(BotUtils.getGuild())) return;
        if (!event.getChannel().getName().contains("ban-")) return;
        BotUtils.getChannel("cBanLogs").sendMessage(event.getChannel().getName() + "\n" +
                event.getMember().getAsMention() + " **»** " + event.getMessage().getContentRaw()).queue();
    }
}
