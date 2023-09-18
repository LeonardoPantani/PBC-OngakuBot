package it.pantani.ongakubot.listeners;

import it.pantani.ongakubot.lavaplayer.GuildMusicManager;
import it.pantani.ongakubot.lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static it.pantani.ongakubot.OngakuBot.getConfigValue;

public class LeaveListener extends ListenerAdapter {
    private final Map<Long, ScheduledExecutorService> serverTimers = new HashMap<>();
    private final long botDelayQuit = Long.parseLong(getConfigValue("BOT_DELAY_QUIT"));

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        super.onGuildVoiceUpdate(event);

        Guild guild = event.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        /*
         Ignore events that are not relevant to us:
         - If we are not connected to a voice channel
         - If a user has not disconnected
         - If it's not the bot itself
         - If the channel the user disconnected from is different from ours
         - If the user disconnected from a null channel
         - If we are not the last remaining member in the channel
        */
        if (!audioManager.isConnected() || event.getNewValue() != null || event.getJDA().getSelfUser().equals(event.getEntity().getUser()) || (audioManager.getConnectedChannel() != null && event.getOldValue() != null && audioManager.getConnectedChannel().getIdLong() != event.getOldValue().getIdLong()) || event.getChannelLeft() == null || event.getChannelLeft().getMembers().size() != 1) {
            return;
        }

        // Handle the event from here
        long serverId = guild.getIdLong();
        if (!serverTimers.containsKey(serverId)) {
            ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
            serverTimers.putIfAbsent(serverId, timer);

            timer.schedule(() -> {
                // the bot exits the channel (and stops playing songs) if there is no one in the current channel except the bot itself
                if (audioManager.getConnectedChannel() != null && audioManager.getConnectedChannel().getMembers().size() == 1) {
                    // Execute the commands below
                    final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);

                    musicManager.scheduler.player.stopTrack();
                    musicManager.scheduler.queue.clear();
                    audioManager.closeAudioConnection();
                }
                serverTimers.remove(serverId); // Remove the timer once it's completed
            }, botDelayQuit, TimeUnit.SECONDS);
        }
    }
}
