/*
 * The MIT License
 *
 * Copyright 2017 Noukkis.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package noukkisBot.wrks.music;

import noukkisBot.wrks.music.visualPlayer.MessageVisualPlayer;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.music.visualPlayer.TopicVisualPlayer;

/**
 *
 * @author Noukkis
 */
public class MusicWrk {

    private static final Map<Guild, MusicWrk> INSTANCES = new HashMap<>();
    private static final Map<PrivateChannel, MusicWrk> INSTANCES_PRIVATE = new HashMap<>();
    public static final AudioPlayerManager APM = new DefaultAudioPlayerManager();

    private final Guild guild;
    private final AudioPlayer ap;
    private final TrackManager tm;

    public static MusicWrk getInstance(Guild guild) {
        if (!INSTANCES.containsKey(guild)) {
            INSTANCES.put(guild, new MusicWrk(guild));
        }
        return INSTANCES.get(guild);
    }

    public static MusicWrk getInstancePM(PrivateChannel pc) {
        return INSTANCES_PRIVATE.get(pc);
    }

    public static void killAll() {
        for (MusicWrk wrk : INSTANCES.values()) {
            wrk.disconnect();
        }
        INSTANCES.clear();
        INSTANCES_PRIVATE.clear();
    }

    private MusicWrk(Guild guild) {
        this.guild = guild;
        this.ap = APM.createPlayer();
        this.tm = new TrackManager(ap);
        ap.addListener(tm);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(ap));
    }

    public boolean connect(VoiceChannel channel) {
        try {
            guild.getAudioManager().openAudioConnection(channel);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean disconnect() {
        if (guild.getSelfMember().getVoiceState().inVoiceChannel()) {
            guild.getAudioManager().closeAudioConnection();
            tm.stop();
            return true;
        }
        return false;
    }

    public boolean isConnected() {
        return guild.getSelfMember().getVoiceState().inVoiceChannel();
    }

    public void loadMusic(CommandEvent event) {
        if (isConnected()) {
            AudioLoadResultHandler alrh = new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    tm.queue(track);
                    event.reactSuccess();
                    tm.getVisualPlayer().update(false);
                    Help.deleteIn(event.getMessage(), 2000);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        tm.queue(track);
                    }
                    event.reactSuccess();
                    tm.getVisualPlayer().update(false);
                    Help.deleteIn(event.getMessage(), 2000);
                }

                @Override
                public void noMatches() {
                    event.reactError();
                    Help.deleteIn(event.getMessage(), 2000);
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.reactError();
                    Help.deleteIn(event.getMessage(), 2000);
                }
            };
            if (!event.getMessage().getAttachments().isEmpty()) {
                for (Message.Attachment a : event.getMessage().getAttachments()) {
                    APM.loadItem(a.getUrl(), alrh);
                }
            } else {
                APM.loadItem(event.getArgs(), alrh);
            }
        } else {
            event.replyError("I'm not connected to any voice channel");
        }
    }

    public void createMessageVisualPlayer(CommandEvent event, String reply) {
        event.getTextChannel().sendMessage(event.getClient().getSuccess() + " " + reply).queue((msg) -> {
            deleteMessageVisualPlayer();
            tm.setVisualPlayer(new MessageVisualPlayer(msg, tm));
        });
    }

    public void deleteMessageVisualPlayer() {
        tm.deleteMessageVisualPlayer();
    }

    public void topicVisualPlayer(CommandEvent event) {
        tm.setVisualPlayer(new TopicVisualPlayer(event.getMessage(), tm));
    }

    public TrackManager getTrackManager() {
        return tm;
    }

    public void searchMusic(CommandEvent event, String prefix, String keywords) {
        if (isConnected()) {
            APM.loadItem(prefix + keywords, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    List<AudioTrack> l = new ArrayList<>();
                    l.add(track);
                    new SearchResult(event, l, keywords).start();
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    new SearchResult(event, playlist.getTracks(), keywords).start();
                }

                @Override
                public void noMatches() {
                    event.reactError();
                    Help.deleteIn(event.getMessage(), 2000);
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.reactError();
                    Help.deleteIn(event.getMessage(), 2000);
                }
            });
        } else {
            event.replyError("I'm not connected to any voice channel");
        }
    }

    public void createMessageVisualPlayerForBlindTest(CommandEvent event, String reply) {
        event.getAuthor().openPrivateChannel().queue((pc) -> {
            INSTANCES_PRIVATE.put(pc, this);
            pc.sendMessage(event.getClient().getSuccess() + " " + reply).queue((msg) -> {
                deleteMessageVisualPlayer();
                tm.setVisualPlayer(new MessageVisualPlayer(msg, tm));
            });
        });
    }
}
