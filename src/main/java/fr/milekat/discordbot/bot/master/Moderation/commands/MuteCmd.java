package fr.milekat.discordbot.bot.master.Moderation.commands;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.Moderation.ModerationUtils;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.utils.DateMileKat;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Date;

public class MuteCmd extends ListenerAdapter {
    public MuteCmd() {
        BotUtils.getGuild().upsertCommand(
                new CommandData("mute", BotUtils.getMsg("mute.slashCmd"))
                        .addOptions(new OptionData(OptionType.USER,
                                        "member",
                                        BotUtils.getMsg("mute.slashOptDescMember"),
                                        true),
                                new OptionData(OptionType.STRING,
                                        "duration",
                                        BotUtils.getMsg("mute.slashOptDescDuration"),
                                        true),
                                new OptionData(OptionType.STRING,
                                        "reason",
                                        BotUtils.getMsg("mute.slashOptDescReason"),
                                        true)
                        ).setDefaultEnabled(false)
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
