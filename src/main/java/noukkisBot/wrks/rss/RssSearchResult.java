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
package noukkisBot.wrks.rss;

import com.sun.syndication.feed.synd.SyndFeed;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.ReactButtonsMaker;

/**
 *
 * @author Noukkis
 */
public class RssSearchResult {

    private static final int MAX = 5;

    private final List<SyndFeed> feeds;
    private final List<SyndFeed> currents;
    private final TextChannel chan;
    private final Member member;
    private final int maxPage;
    private int page;

    public RssSearchResult(TextChannel chan, Member member, List<SyndFeed> feeds) {
        this.feeds = feeds;
        this.currents = new ArrayList<>();
        this.chan = chan;
        this.member = member;
        this.maxPage = (feeds.size() / MAX);
        this.page = 0;
    }

    void start() {
        ReactButtonsMaker rbm = ReactButtonsMaker.getInstance();
        chan.sendMessage(createMsg()).queue((msg) -> {
            rbm.add(msg, "❌", (event) -> stop(msg));
            rbm.add(msg, "◀", (event) -> previous(msg));
            int max = feeds.size() > MAX ? MAX : feeds.size();
            for (int i = 0; i < max; i++) {
                final int j = i;
                rbm.add(msg, Help.NUMBERS_REACTS[i + 1], (event) -> select(j, msg));
            }
            rbm.add(msg, "▶", (event) -> next(msg));
        });
    }

    private String createMsg() {
        currents.clear();
        int min = page * MAX;
        String msg = "**Available Feeds**\n```markdown\n";

        for (int i = min; i < min + MAX && i < feeds.size(); i++) {
            currents.add(feeds.get(i));
            String title = feeds.get(i).getTitle();
            msg += "\n" + (i - min + 1) + ". " + title;
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
        RssWrk wrk = RssWrk.getInstance(chan.getGuild());
        int index = page * MAX + i;
        if (index < feeds.size()) {
            wrk.addFeed(feeds.get(index).getUri(), member);
            stop(msg);
        }
    }

    private void stop(Message msg) {
        msg.delete().queue();
    }

}
