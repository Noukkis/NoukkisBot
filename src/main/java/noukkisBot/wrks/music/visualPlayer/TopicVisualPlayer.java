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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.text.DecimalFormat;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.requests.RestAction;
import noukkisBot.wrks.RestActionScheduler;
import noukkisBot.wrks.music.TrackManager;

/**
 *
 * @author Noukkis
 */
public class TopicVisualPlayer extends VisualPlayer {

    private final static String RADIO = "🔘";
    private final static String TIRET = "▬";
    private final static int RAPPORT = 2;
    
    
    private final TrackManager tm;
    private final String originalTopic;
    private final RestActionScheduler<Void> ras;

    private TextChannel channel;

    public TopicVisualPlayer(Message msg, TrackManager tm) {
        this.channel = msg.getTextChannel();
        this.tm = tm;
        this.originalTopic = channel.getTopic();
        this.ras = new RestActionScheduler<>(1000);
    }

    @Override
    public void update(boolean now) {
        if (channel != null) {
            AudioTrack cur = tm.getAudioPlayer().getPlayingTrack();
            String update = "No current track";
            if (cur != null) {
                String playing = (tm.getAudioPlayer().isPaused() ? "⏸" : "▶") + "\t";
                update = playing + cur.getInfo().title + time(cur);
            }
            ras.schedule(channel.getManager().setTopic(update), now, null, (t) -> delete());
        }
    }

    @Override
    public void delete() {
        if (channel != null) {
            channel.getManager().setTopic(originalTopic).queue();
            channel = null;
        }
    }

    @Override
    public TextChannel getChannel() {
        return channel;
    }

}
