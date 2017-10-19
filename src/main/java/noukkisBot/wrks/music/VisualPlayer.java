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

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.text.DecimalFormat;
import net.dv8tion.jda.core.entities.Message;
import noukkisBot.helpers.Help;

/**
 *
 * @author Noukkis
 */
public class VisualPlayer {

    private final static DecimalFormat DF = new DecimalFormat("00");

    private Message msg;
    private final TrackManager tm;

    public VisualPlayer(Message msg, TrackManager tm) {
        this.msg = msg;
        this.tm = tm;
        Help.RBM.addReactionButton(msg, "⏹", (event) -> tm.clear());
        Help.RBM.addReactionButton(msg, "⏯", (event) -> tm.pauseStart());
        Help.RBM.addReactionButton(msg, "⏭", (event) -> tm.nextTrack());
        Help.RBM.addReactionButton(msg, "🔀", (event) -> tm.shuffle());
    }

    public void update() {
        if (msg != null) {
            AudioTrack cur = tm.getAudioPlayer().getPlayingTrack();
            String update = "No current track";
            if (cur != null) {
                update = "";
                if (!tm.getQueue().isEmpty()) {
                    update += "**Queue**\n```Markdown";
                    int i = 1;
                    for (AudioTrack track : tm.getQueue()) {
                        update += "\n" + i + ". " + track.getInfo().title;
                        i++;
                        if (i > 5) {
                            int more = (tm.getQueue().size() - 5);
                            if(more > 0) {
                            update += "\n\tand " + more + " more...";
                            }
                            break;
                        }
                    }
                    update += "\n```\n";
                }
                update += "**Currently Playing : **" + cur.getInfo().title;
                int durM = (int) ((cur.getDuration() / 1000) / 60);
                int durS = (int) ((cur.getDuration() / 1000) % 60);

            }
            msg.editMessage(update).queue();
        }
    }

    public void stop() {
        msg.delete().queue();
        msg = null;
    }

}
