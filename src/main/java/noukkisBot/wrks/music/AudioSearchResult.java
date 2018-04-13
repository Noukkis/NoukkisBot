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
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.ReactButtonsMaker;

/**
 *
 * @author Noukkis
 */
public class AudioSearchResult {

    private static final int MAX = 5;

    private final List<AudioTrack> tracks;
    private final List<AudioTrack> currents;
    private final TextChannel chan;
    private final String keyword;
    private final int maxPage;
    private final User author;
    private int page;

    public AudioSearchResult(CommandEvent ce, List<AudioTrack> tracks, String keywords) {
        this.tracks = tracks;
        this.currents = new ArrayList<>();
        this.chan = ce.getTextChannel();
        this.keyword = keywords;
        this.maxPage = (tracks.size() / MAX) + 1;
        this.page = 0;
        this.author = ce.getAuthor();
    }

    void start() {
        ReactButtonsMaker rbm = ReactButtonsMaker.getInstance();
        chan.sendMessage(createMsg()).queue((msg) -> {
            rbm.add(msg, "❌", author, (event) -> stop(msg));
            rbm.add(msg, "◀", author, (event) -> previous(msg));
            int max = tracks.size() > MAX ? MAX : tracks.size();
            for (int i = 0; i < max; i++) {
                final int j = i;
                rbm.add(msg, Help.NUMBERS_REACTS[i + 1], author, (event) -> select(j, msg));
            }
            rbm.add(msg, "▶", author, (event) -> next(msg));
        });
    }

    private String createMsg() {
        currents.clear();
        int min = page * MAX;
        String msg = "**Search Results**\n";
        msg += "for keywords \"" + keyword + "\"```markdown\n";

        for (int i = min; i < min + 5 && i < tracks.size(); i++) {
            currents.add(tracks.get(i));
            AudioTrackInfo infos = tracks.get(i).getInfo();
            msg += "\n" + (i - min + 1) + ". " + infos.title;
        }
        return msg + "```\nPage " + (page + 1) + "/" + maxPage;
    }

    private void previous(Message msg) {
        page--;
        if (page < 0) {
            page = maxPage - 1;
        }
        msg.editMessage(createMsg()).queue();
    }

    private void next(Message msg) {
        page++;
        if (page >= maxPage) {
            page = 0;
        }
        msg.editMessage(createMsg()).queue();
    }

    private void select(int i, Message msg) {
        MusicWrk wrk = MusicWrk.getInstance(chan.getGuild());
        TrackManager tm = wrk.getTrackManager();
        int index = page * MAX + i;
        if (index < tracks.size()) {
            tm.queue(tracks.get(index));
            stop(msg);
        }
    }

    private void stop(Message msg) {
        msg.delete().queue();
    }

}
