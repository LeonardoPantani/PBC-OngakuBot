package it.pantani.ongakubot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import it.pantani.ongakubot.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerManager {
    private static PlayerManager INSTANCE;

    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);

            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());

            return guildMusicManager;
        });
    }

    public void loadAndPlay(InteractionHook hook, Guild guild, String trackUrl) {
        final GuildMusicManager musicManager = this.getMusicManager(guild);

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);

                hook.sendMessageEmbeds(
                    Utils.createEmbed("play", Color.GREEN, "Added to the queue: [" + track.getInfo().title + "](" + track.getInfo().uri + ") di `" + track.getInfo().author + "`"))
                    .addActionRow(
                        Button.success("rewind", "↩️ Rewind"),
                        Button.danger("stop", "⏹️ Remove"),
                        Button.primary("pause", "⏯️ Resume/Pause"),
                        Button.secondary("skip", "⏭️ Skip"),
                        Button.secondary("queue", "⏬ Queue")
                    )
                    .setEphemeral(false)
                    .queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                if(playlist.isSearchResult()) { // se è il risultato della ricerca ne carico una, altrimenti tutte
                    AudioTrack track = tracks.get(0);
                    musicManager.scheduler.queue(track);

                    hook.sendMessageEmbeds(
                                    Utils.createEmbed("play", Color.GREEN, "Added to the queue: [" + track.getInfo().title + "](" + track.getInfo().uri + ") di `" + track.getInfo().author + "`"))
                            .addActionRow(
                                    Button.success("rewind", "↩️ Rewind"),
                                    Button.danger("stop", "⏹️ Remove"),
                                    Button.primary("pause", "⏯️ Resume/Pause"),
                                    Button.secondary("skip", "⏭️ Skip"),
                                    Button.secondary("queue", "⏬ Queue")
                            )
                            .setEphemeral(false)
                            .queue();
                } else { // carico tutto
                    for (final AudioTrack track : tracks) {
                        musicManager.scheduler.queue(track);
                    }

                    hook.sendMessageEmbeds(
                            Utils.createEmbed("play", Color.GREEN, "Added to the queue `" + tracks.size() + "` tracks from the playlist `" + trackUrl.replace("ytsearch:", "") + "`"))
                            .addActionRow(
                                    Button.success("rewind", "↩️ Rewind"),
                                    Button.danger("stop", "⏹️ Remove"),
                                    Button.primary("pause", "⏯️ Resume/Pause"),
                                    Button.secondary("skip", "⏭️ Skip"),
                                    Button.secondary("queue", "⏬ Queue")
                            )
                            .setEphemeral(false)
                            .queue();
                }
            }

            @Override
            public void noMatches() {
                hook.sendMessageEmbeds(Utils.createEmbed("play", Color.RED, "Cannot obtain audio source.")).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                hook.sendMessageEmbeds(Utils.createEmbed("play", Color.RED, "Loading failed.")).queue();
            }
        });
    }

    public void loadAndPlay(GuildVoiceState guildVoiceState, String trackUrl) { // da console
        final GuildMusicManager musicManager = this.getMusicManager(guildVoiceState.getGuild());

        this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();

                for (final AudioTrack track : tracks) {
                    musicManager.scheduler.queue(track);
                }
            }

            @Override
            public void noMatches() {
                //
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                //
            }
        });
    }

    public static PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }

        return INSTANCE;
    }

}
