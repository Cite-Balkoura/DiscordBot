package fr.milekat.discordbot.bot.master.features;

import fr.milekat.discordbot.bot.BotUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

import javax.annotation.Nonnull;

public class ProfileRegister extends ListenerAdapter {
    /**
     * Add button on new message in Event channel
     */
    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getChannel().equals(BotUtils.getChannel("cRegister"))) return;
        event.getMessage().getButtons().add(Button.success(event.getAuthor().getName(), BotUtils.getMsg("profileReg.buttonFrom")).withEmoji(Emoji.fromUnicode("\uD83D\uDCDD")));
    }

    /**
     * When a member of guild click on buttonAccountRegister to register his profile
     */
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if (!event.getChannel().equals(BotUtils.getChannel("cRegister"))) return;
        if (event.getMember().getRoles().contains(BotUtils.getRole("rProfile"))) return;
        if (event.getMember().getRoles().contains(BotUtils.getRole("rWaiting"))) return;
        openForm(event.getMember());
    }

    /**
     *
     */
    @Override
    public void onSelectionMenu(@Nonnull SelectionMenuEvent event) {

    }

    /**
     * Open a new form (Ticket)
     */
    private void openForm(Member member) {
        BotUtils.getCategory("ccRegister").createTextChannel(member.getEffectiveName()).queue((textChannel) ->
                textChannel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue((ignore) -> {
            // TODO: 01/07/2021 Process steps, start from here
        }));
    }
}
