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
package noukkisBot.wrks.music.visualPlayer;

import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.awt.Color;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import noukkisBot.wrks.ReactButtonsMaker;
import noukkisBot.wrks.RestActionScheduler;
import noukkisBot.wrks.music.TrackManager;

/**
 *
 * @author Noukkis
 */
public class MessageVisualPlayer extends VisualPlayer {

    protected final TrackManager tm;
    private final RestActionScheduler<Message> ras;
    
    private Message msg;

    public MessageVisualPlayer(Message msg, TrackManager tm) {
        this.msg = msg;
        this.tm = tm;
        this.ras = new RestActionScheduler<>(1000);
        ReactButtonsMaker rbm = ReactButtonsMaker.getInstance();
        rbm.add(msg, "⏹", (event) -> tm.clear());
        rbm.add(msg, "⏯", (event) -> tm.pauseContinue());
        rbm.add(msg, "⏭", (event) -> tm.nextTrack());
        rbm.add(msg, "🔀", (event) -> tm.shuffle());
    }

    @Override
    public void update(boolean now) {
        if (msg != null) {
            AudioTrack cur = tm.getAudioPlayer().getPlayingTrack();
            String playing = tm.getAudioPlayer().isPaused() ? "⏸" : "▶";
            MessageBuilder msgBuilder = new MessageBuilder();
            EmbedBuilder emBuilder = new EmbedBuilder()
                    .setTitle(playing + " No current track");
            if (cur != null) {
                emBuilder
                        .setTitle(playing + " " + cur.getInfo().title, cur.getInfo().uri)
                        .setDescription("**Time** : " + time(cur))
                        .setColor(getColor(cur));
                if (!tm.getQueue().isEmpty()) {
                    msgBuilder.append(getQueue());
                }

            }
            ras.schedule(msg.editMessage(msgBuilder.setEmbed(emBuilder.build()).build()), now, null, (t) -> delete());
        }
    }

    private String getQueue() {
        String queue = "\n\n**Queue**\n```Markdown";
        int i = 1;
        for (AudioTrack track : tm.getQueue()) {
            queue += "\n" + i + ". " + track.getInfo().title;
            i++;
            if (i > 5) {
                int more = (tm.getQueue().size() - 5);
                if (more > 0) {
                    queue += "\n\n";
                    for (int j = 0; j < 16; j++) {
                        queue += "\t";
                    }
                    queue += "and " + more + " more...";
                }
                break;
            }
        }
        return queue + "```";
    }

    private Color getColor(AudioTrack cur) {
        Color c = Color.GRAY;
        if (cur instanceof YoutubeAudioTrack) {
            c = Color.RED;
        } else if (cur instanceof VimeoAudioTrack) {
            c = new Color(0, 173, 239);
        } else if (cur instanceof TwitchStreamAudioTrack) {
            c = new Color(100, 65, 164);
        } else if (cur instanceof SoundCloudAudioTrack) {
            c = new Color(255, 85, 16);
        } else if (cur instanceof BandcampAudioTrack) {
            c = new Color(98, 154, 169);
        } else if (cur instanceof NicoAudioTrack) {
            c = Color.BLACK;
        }
        return c;
    }

    @Override
    public void delete() {
        if (msg != null) {
            msg.delete().queue();
            msg = null;
        }
    }

    @Override
    public TextChannel getChannel() {
        return msg.getTextChannel();
    }

}
