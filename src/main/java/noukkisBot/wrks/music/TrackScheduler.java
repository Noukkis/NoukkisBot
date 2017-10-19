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

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Noukkis
 */
public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer ap;
    private final Queue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer ap) {
        super();
        this.ap = ap;
        this.queue = new LinkedList<>();
    }
    
    public void clear() {
        queue.clear();
        ap.stopTrack();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if(!queue.isEmpty()) {
                player.playTrack(queue.poll());
            }
        }
    }

    public void queue(AudioTrack track) {
        if (queue.isEmpty() && ap.getPlayingTrack() == null) {
            play(track);
        } else {
            queue.add(track);
        }
    }

    private void play(AudioTrack track) {
        ap.playTrack(track);
    }

    public AudioPlayer getAudioPlayer() {
        return ap;
    }

    public Queue<AudioTrack> getQueue() {
        return queue;
    }

}
