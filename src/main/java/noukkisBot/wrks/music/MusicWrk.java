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

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 *
 * @author Noukkis
 */
public class MusicWrk {

    public static final Map<Guild, MusicWrk> INSTANCES = new HashMap<>();
    public static final AudioPlayerManager APM = new DefaultAudioPlayerManager();

    private final Guild guild;
    private VisualPlayer vp;
    private final AudioPlayer ap;
    private final TrackScheduler ts;

    public static MusicWrk getInstance(Guild guild) {
        if (!INSTANCES.containsKey(guild)) {
            INSTANCES.put(guild, new MusicWrk(guild));
        }
        return INSTANCES.get(guild);
    }

    public MusicWrk(Guild guild) {
        this.guild = guild;
        this.ap = APM.createPlayer();
        this.ts = new TrackScheduler(ap);
        ap.addListener(ts);
        guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(ap));
    }

    public boolean connect(VoiceChannel channel) {
        if (channel != null) {
            try {
                guild.getAudioManager().openAudioConnection(channel);
                return true;
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean disconnect() {
        if (guild.getSelfMember().getVoiceState().inVoiceChannel()) {
            guild.getAudioManager().closeAudioConnection();
            vp.stop();
            vp = null;
            return true;
        }
        return false;
    }

    public boolean isConnected() {
        return guild.getSelfMember().getVoiceState().inVoiceChannel();
    }

    public void loadMusic(CommandEvent event) {
        if (isConnected()) {
            APM.loadItem(event.getArgs(), new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    ts.queue(track);
                    event.reactSuccess();
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                        ts.queue(track);
                    }
                    event.reactSuccess();
                }

                @Override
                public void noMatches() {
                    event.reactError();
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.reactError();
                }
            });
        } else {
            event.reactWarning();
        }
    }

    public void createVisualPlayer(CommandEvent event) {
        event.getMessage().getTextChannel().sendMessage("Joined" + event.getClient().getSuccess()).queue((msg) -> {
            vp = new VisualPlayer(msg, ts);
            vp.start();
        });
    }
}
