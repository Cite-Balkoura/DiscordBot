package fr.milekat.discordbot.bot.master.features.Moderation;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Mute;
import fr.milekat.discordbot.bot.master.managers.MuteManager;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import fr.milekat.discordbot.utils.DateMileKat;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Date;

public class MuteCommand extends ListenerAdapter {
    public MuteCommand() {
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
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (!event.getName().equalsIgnoreCase("mute")) return;
        if (!event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
            event.reply(BotUtils.getMsg("noPerm")).setEphemeral(true).queue();
            return;
        }
        Member targetMember = event.getOption("member").getAsMember();
        //  Check if target has a profile
        if (!ProfileManager.exists(event.getOption("member").getAsMember().getIdLong())) {
            event.reply(BotUtils.getMsg("mute.slashNoProfile")).setEphemeral(true).queue();
            BotUtils.getGuild().addRoleToMember(targetMember, BotUtils.getRole("rMute")).queue();
            return;
        }
        long muteDelay = DateMileKat.parsePeriod(event.getOption("duration").getAsString()) + new Date().getTime();
        //  Check if mute less than 10s (10000ms)
        if (muteDelay < (new Date().getTime()+10000)){
            event.reply(BotUtils.getMsg("mute.slashDelayToLow")).setEphemeral(true).queue();
            return;
        }
        MuteManager.save(new Mute(ProfileManager.getProfile(event.getOption("member").getAsMember().getIdLong()),
                new Date(),
                new Date(muteDelay),
                event.getOption("reason").getAsString()));
        BotUtils.getGuild().addRoleToMember(targetMember, BotUtils.getRole("rMute")).queue();
        event.reply(BotUtils.getMsg("mute.slashSuccess")).setEphemeral(true).queue();
    }
}
