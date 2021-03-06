package fr.milekat.discordbot.bot.master.moderation.commands;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.moderation.ModerationUtils;
import fr.milekat.utils.DateMileKat;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Date;

public class MuteCmd extends ListenerAdapter {
    public MuteCmd() {
        BotUtils.getGuild().upsertCommand(BotUtils.getCommand("mute.muteCmd").setDefaultEnabled(false)
        ).queue(command -> BotUtils.getGuild().updateCommandPrivilegesById(command.getIdLong(),
                new CommandPrivilege(CommandPrivilege.Type.ROLE, true, BotUtils.getRole("rAdmin").getIdLong())
        ).queue());
    }

    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("mute")) return;
        if (ModerationUtils.cantProcessDelay(event)) return;
        ModerationUtils.muteSend(event.getOption("member").getAsMember(),
                ProfileManager.getProfile(event.getOption("member").getAsMember().getIdLong()),
                ProfileManager.getProfile(event.getUser().getIdLong()),
                DateMileKat.parsePeriod(event.getOption("duration").getAsString()) + new Date().getTime(),
                event.getOption("reason").getAsString());
        event.reply(BotUtils.getMsg("mute.slashSuccess")).setEphemeral(true).queue();
    }
}
