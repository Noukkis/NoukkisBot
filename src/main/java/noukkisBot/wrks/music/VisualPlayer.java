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
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.core.entities.Message;
import noukkisBot.helpers.Help;

/**
 *
 * @author Noukkis
 */
public class VisualPlayer {

    private final static DecimalFormat DF = new DecimalFormat("00");
    private final static String LINE_CHAR = "⹀";
    private final static String RADIO_BUTTON = "🔘";
    private final static int RADIO_BUTTON_SIZE = 7;

    private Message msg;
    private final TrackManager tm;
    private Timer timer;

    public VisualPlayer(Message msg, TrackManager tm) {
        this.msg = msg;
        this.tm = tm;
        this.timer = new Timer();
        Help.RBM.addReactionButton(msg, "⏹", (event) -> tm.clear());
        Help.RBM.addReactionButton(msg, "⏯", (event) -> tm.pauseStart());
        Help.RBM.addReactionButton(msg, "⏭", (event) -> tm.nextTrack());
        Help.RBM.addReactionButton(msg, "🔀", (event) -> tm.shuffle());
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 5000, 2000);
    }

    public void update() {
        if (msg != null) {
            AudioTrack cur = tm.getAudioPlayer().getPlayingTrack();
            String update = "No current track";
            if (cur != null) {
                String playing = tm.getAudioPlayer().isPaused() ? "⏸" : "▶";
                update = playing + " **Currently Playing : **" + cur.getInfo().title
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
                                update += "\n\tand " + more + " more...";
                            }
                            break;
                        }
                    }
                    update += "\n```";
                }

            }
            msg.editMessage(update).queue();
        }
    }

    private String time(long pos, long dur) {
        int posM = (int) ((pos / 1000) / 60);
        int posS = (int) ((pos / 1000) % 60);
        int durM = (int) ((dur / 1000) / 60);
        int durS = (int) ((dur / 1000) % 60);
        return " [" + DF.format(posM) + ":" + DF.format(posS) + " / "
                + DF.format(durM) + ":" + DF.format(durS) + "]";
    }

    public void stop() {
        msg.delete().queue();
        timer.cancel();
    }

}
