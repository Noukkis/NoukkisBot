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
package noukkisBot.commands.rss;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.Command.Category;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.sun.syndication.feed.synd.SyndFeed;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import noukkisBot.wrks.rss.RssWrk;

/**
 *
 * @author Noukkis
 */
public class List extends Command {

    public List() {
        this.name = "rsslist";
        this.category = new Category("RSS");
        this.help = "List the RSS feeds of this Guild";
    }

    @Override
    protected void execute(CommandEvent event) {
        Map<String, SimpleEntry<SyndFeed, java.util.List<Member>>>  feeds = RssWrk.getInstance(event.getGuild()).getFeeds();
        int i = 1;
        MessageBuilder builder = new MessageBuilder("RSS Feeds List :\n");
        String content = "";
        for (Map.Entry<String, SimpleEntry<SyndFeed, java.util.List<Member>>> entry : feeds.entrySet()) {
            String address = entry.getKey();
            String format = entry.getValue().getValue().contains(event.getMember())
                    ? "+" : "-";
            SyndFeed feed = entry.getValue().getKey();
            content += "\n" + format + "    " + i + ". " + feed.getTitle();
            i++;
        }
        builder.appendCodeBlock(content, "diff");
        event.reply(builder.build());
    }

}
