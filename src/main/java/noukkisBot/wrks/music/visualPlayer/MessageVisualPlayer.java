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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.music.TrackManager;

/**
 *
 * @author Noukkis
 */
public class MessageVisualPlayer extends VisualPlayer {

    private Message msg;
    protected final TrackManager tm;

    public MessageVisualPlayer(Message msg, TrackManager tm) {
        this.msg = msg;
        this.tm = tm;
        Help.RBM.add(msg, "⏹", (event) -> tm.clear());
        Help.RBM.add(msg, "⏯", (event) -> tm.pauseContinue());
        Help.RBM.add(msg, "⏭", (event) -> tm.nextTrack());
        Help.RBM.add(msg, "🔀", (event) -> tm.shuffle());
    }

    @Override
    public void update() {
        if (msg != null) {
            AudioTrack cur = tm.getAudioPlayer().getPlayingTrack();
            String playing = tm.getAudioPlayer().isPaused() ? "⏸" : "▶";
            String update = playing + " No current track";
            if (cur != null) {
                update = playing + " " + cur.getInfo().title
                        + " " + time(cur.getPosition(), cur.getDuration());
                if (!tm.getQueue().isEmpty()) {
                    update += "\n\n**Queue**\n```Markdown";
                    int i = 1;
                    for (AudioTrack track : tm.getQueue()) {
                        update += "\n" + i + ". " + track.getInfo().title;
                        i++;
                        if (i > 5) {
                            int more = (tm.getQueue().size() - 5);
                            if (more > 0) {
                                update += "\n```and " + more + " more...";
                            }
                            break;
                        }
                    }
                }

            }
            msg.editMessage(update).queue(null, (t) -> delete());
        }
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
