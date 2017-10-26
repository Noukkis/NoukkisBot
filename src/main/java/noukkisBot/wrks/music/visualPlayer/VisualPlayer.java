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
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Noukkis
 */
public abstract class VisualPlayer {

    private final static DecimalFormat DF = new DecimalFormat("00");

    public abstract void update(boolean now);

    public abstract void delete();

    public abstract TextChannel getChannel();

    protected String time(AudioTrack cur) {
        long pos = cur.getPosition();
        long dur = cur.getDuration();
        int posM = (int) ((pos / 1000) / 60);
        int posS = (int) ((pos / 1000) % 60);
        int durM = (int) ((dur / 1000) / 60);
        int durS = (int) ((dur / 1000) % 60);
        return " [" + DF.format(posM) + ":" + DF.format(posS) + " / "
                + DF.format(durM) + ":" + DF.format(durS) + "]";
    }
}
