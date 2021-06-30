package fr.milekat.discordbot.bot.master.features;

import fr.milekat.discordbot.bot.BotManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountRegister extends ListenerAdapter {
    /* Main */
    private final BotManager manager;
    private final JDA jda;
    private final JSONObject id;
    private final JSONObject msg;
    private final List<String> endSteps = new ArrayList<>(Arrays.asList("WAITING","ACCEPTED", "REFUSED"));
    private final ArrayList<TextChannel> listChannels = new ArrayList<>();
    /* Guilds */
    private final Guild gStaff;
    private final Guild gPublic;
    /* Roles */
    private final Role rWaiting;
    private final Role rValid;
    private final Role rAdmin;
    private final Role rTeam;
    /* Staff Channels */
    private final TextChannel cCandid;
    private final TextChannel cAccept;
    private final TextChannel cDeny;
    /* Public Channels */
    private final TextChannel cRegister;

    public AccountRegister(BotManager manager, JDA jda, JSONObject id, JSONObject msg) {
        this.manager = manager;
        this.jda = jda;
        this.id = id;
        this.msg = msg;
        this.gStaff = jda.getGuildById((Long) id.get("gStaff"));
        this.gPublic = jda.getGuildById((Long) id.get("gPublic"));
        this.rWaiting = jda.getRoleById((Long) id.get("rWaiting"));
        this.rValid = jda.getRoleById((Long) id.get("rValid"));
        this.rAdmin = jda.getRoleById((Long) id.get("rAdmin"));
        this.rTeam = jda.getRoleById((Long) id.get("rTeam"));
        this.cRegister = jda.getTextChannelById((Long) id.get("cRegister"));
        this.cCandid = jda.getTextChannelById((Long) id.get("cCandid"));
        this.cAccept = jda.getTextChannelById((Long) id.get("cAccept"));
        this.cDeny = jda.getTextChannelById((Long) id.get("cDeny"));
        this.listChannels.add(cRegister);
        this.listChannels.add(cCandid);
        this.listChannels.add(cAccept);
        this.listChannels.add(cDeny);
    }
}
