package fr.milekat.discordbot.bot.master.features.Moderation;

import fr.milekat.discordbot.bot.BotUtils;
import fr.milekat.discordbot.bot.master.classes.Mute;
import fr.milekat.discordbot.bot.master.classes.Profile;
import fr.milekat.discordbot.bot.master.managers.MuteManager;
import fr.milekat.discordbot.bot.master.managers.ProfileManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

import javax.annotation.Nonnull;
import java.util.Date;

public class UnMuteFeature extends ListenerAdapter {
    public UnMuteFeature() {
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
        if (event.getUser().isBot() || !event.getGuild().equals(BotUtils.getGuild())) return;
        if (!event.getName().equalsIgnoreCase("unmute")) return;
        if (!event.getMember().getRoles().contains(BotUtils.getRole("rAdmin"))) {
            event.reply(BotUtils.getMsg("noPerm")).setEphemeral(true).queue();
            return;
        }
        Member targetMember = event.getOption("member").getAsMember();
        //  Check if target has a profile
        if (!ProfileManager.exists(event.getOption("member").getAsMember().getIdLong())) {
            event.reply(BotUtils.getMsg("mute.slashNoProfile")).setEphemeral(true).queue();
            return;
        }
        Profile targetProfile = ProfileManager.getProfile(targetMember.getIdLong());
        if (!MuteManager.isMuted(targetProfile)) {
            event.reply(BotUtils.getMsg("mute.notMuted")).setEphemeral(true).queue();
            return;
        }
        Mute oldMute = MuteManager.getLastMute(targetProfile);
        Mute updatedMute = new Mute(targetProfile, oldMute.getMuteDate(), new Date(), oldMute.getReasonMute());
        MuteManager.save(oldMute.setAcknowledge(true));
        MuteManager.save(updatedMute.setReasonPardon(event.getOption("reason").getAsString()).setAcknowledge(true));
        BotUtils.getGuild().removeRoleFromMember(targetMember, BotUtils.getRole("rMute")).queue();
        event.reply(BotUtils.getMsg("mute.slashSuccess")).setEphemeral(true).queue();
    }
}
