package fr.milekat.discordbot.bot.master.moderation.commands;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.core.managers.ProfileManager;
import fr.milekat.discordbot.bot.master.moderation.ModerationUtils;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;

public class UnMuteCmd extends ListenerAdapter {
    public UnMuteCmd() {
        BotUtils.getGuild().upsertCommand(
                new CommandData("unmute", BotUtils.getMsg("mute.slashUnMute"))
                        .addOptions(new OptionData(OptionType.USER,
                                        "member",
                                        BotUtils.getMsg("mute.slashOptDescMember"),
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

    /**
     * /unmute command
     */
    @Override
    public void onSlashCommand(@Nonnull SlashCommandEvent event) {
        if (!event.getName().equalsIgnoreCase("unmute")) return;
        if (ModerationUtils.cantProcess(event)) return;
        ModerationUtils.unMuteSend(event.getOption("member").getAsMember(),
                ProfileManager.getProfile(event.getOption("member").getAsMember().getIdLong()),
                event.getOption("reason").getAsString());
        event.reply(BotUtils.getMsg("mute.slashSuccess")).setEphemeral(true).queue();
    }
}
