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
import java.util.Timer;
import java.util.TimerTask;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.entities.Message;

/**
 *
 * @author Noukkis
 */
@RequiredArgsConstructor
public class VisualPlayer {

    private final Message msg;
    private final TrackScheduler ts;
    private boolean running;

    public void start() {
        running = true;
        new Thread(() -> {
            while (running) {
                update();
            }
        }).start();
    }

    private void update() {
        AudioTrack cur = ts.getAudioPlayer().getPlayingTrack();
        String update = msg.getContent();
        if (cur != null) {
            update = "**Currently Playing :**" + cur.getInfo().title;
            int posM = (int) ((cur.getPosition() / 1000) / 60);
            int posS = (int) ((cur.getPosition() / 1000) % 60);
            int durM = (int) ((cur.getDuration() / 1000) / 60);
            int durS = (int) ((cur.getDuration() / 1000) % 60);
            update += "(" + posM + ":" + posS + " / " + durM + ":" + durS + ")";
            if (!ts.getQueue().isEmpty()) {
                update += "\n**Queue**\n```Java";
                int i = 1;
                for (AudioTrack track : ts.getQueue()) {
                    update += "\n" + i + ". " + track.getInfo().title;
                    i++;
                    if (i > 5) {
                        update += "\n```";
                        break;
                    }
                }
            }
        }
        msg.editMessage(update).queue();
    }

    public void stop() {
        running = false;
    }

}
