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

import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Noukkis
 */
public class VisualPlayerWrapper extends VisualPlayer {

    private VisualPlayer msg;
    private VisualPlayer topic;
    private Timer timer;

    public VisualPlayerWrapper() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 5000, 2000);
    }

    public void set(VisualPlayer vp) {
        if (vp instanceof MessageVisualPlayer) {
            deleteMessage();
            msg = vp;
        } else {
            if (topic != null) {
                if (vp.getChannel().getId().equals(topic.getChannel().getId())) {
                    vp.delete();
                    topic.delete();
                    topic = null;
                } else {
                    topic.delete();
                    topic = vp;
                }
            } else {
                topic = vp;
            }
        }
    }

    public void deleteMessage() {
        if (msg != null) {
            msg.delete();
            msg = null;
        }
    }

    @Override
    public void update() {
        if (msg != null) {
            msg.update();
        }
        if (topic != null) {
            topic.update();
        }
    }

    @Override
    public void delete() {
        if (msg != null) {
            msg.delete();
            msg = null;
        }
        if (topic != null) {
            topic.delete();
            topic = null;
        }
        timer.cancel();
    }

    @Override
    public TextChannel getChannel() {
        return (msg != null) ? msg.getChannel() : (topic != null) ? topic.getChannel() : null;
    }

}
