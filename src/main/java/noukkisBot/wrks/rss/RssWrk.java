/*
 * The MIT License
 *
 * Copyright 2018 Noukkis.
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

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import noukkisBot.helpers.ExtendedThread;
import noukkisBot.helpers.Help;
import noukkisBot.helpers.SearchResult;
import noukkisBot.wrks.GuildontonManager.Guildonton;
import org.jdom.Element;

/**
 *
 * @author Noukkis
 */
public class RssWrk extends ExtendedThread implements Guildonton {

    private static final int SLEEP = 1000 * 3;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final Map<String, List<Long>> feeds;

    private long chan;

    private transient Date lastFetch;
    private transient Guild guild;
    private transient SyndFeedInput input;

    public RssWrk() {
        super(SLEEP);
        this.feeds = new ConcurrentHashMap<>();
        this.chan = -1;
    }

    @Override
    public void init(Guild guild) {
        this.guild = guild;
        this.input = new SyndFeedInput();
        if (chan < 0) {
            this.chan = guild.getDefaultChannel().getIdLong();
        }
        this.lastFetch = new Date();
        setName("RSS-" + guild.getName());
        start();
    }

    @Override
    public void kill() {
        close();
    }

    public Map<String, List<Long>> getFeeds() {
        return feeds;
    }

    @Override
    public void execute() {
        Date now = new Date();
        fetch();
        lastFetch = now;
    }

    public void setChan(TextChannel chan) {
        this.chan = chan.getIdLong();
    }

    public TextChannel getChan() {
        return guild.getTextChannelById(chan);
    }

    public boolean addFeed(String address, Member member) {
        SyndFeed feed = createFeed(address);
        if (feed == null) {
            return false;
        }
        feed.setUri(address);
        if (feeds.containsKey(address)) {
            feeds.get(address).add(member.getUser().getIdLong());
        } else {
            ArrayList<Long> list = new ArrayList<>();
            list.add(member.getUser().getIdLong());
            feeds.put(address, list);
        }
        return true;
    }

    public boolean removeFeed(String address, Member member) {
        if (feeds.containsKey(address)) {
            boolean res = feeds.get(address).remove(member.getUser().getIdLong());
            if (feeds.get(address).isEmpty()) {
                feeds.remove(address);
            }
            return res;
        }
        return false;
    }

    public boolean deleteFeed(String address) {
        return feeds.remove(address) != null;
    }

    private void fetch() {
        feeds.forEach((key, value) -> {
            try {
                SyndFeed feed = createFeed(key);
                feed.setUri(key);
                for (Object feedEntry : feed.getEntries()) {
                    SyndEntryImpl se = (SyndEntryImpl) feedEntry;
                    if (se.getPublishedDate().after(lastFetch) && !se.getPublishedDate().after(new Date())) {
                        writeMessageForEntry(se, value);
                    }
                }
            } catch (Exception ex) {
                Help.LOGGER.error("Error fetching " + key, ex);
            }
        });
    }

    private void writeMessageForEntry(SyndEntryImpl se, List<Long> subs) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(se.getTitle(), se.getUri())
                .setAuthor(se.getAuthor())
                .setFooter(DATE_FORMAT.format(se.getPublishedDate()), null);
        for (Element elem : (List<Element>) se.getForeignMarkup()) {
            builder.addField(elem.getName(), elem.getText(), true);
        }
        MessageBuilder msgBuilder = new MessageBuilder(builder);
        for (Long sub : subs) {
            msgBuilder.append(guild.getMemberById(sub));
        }
        getChan().sendMessage(msgBuilder.build()).queue();
    }

    public boolean search(Member member, TextChannel channel) {
        ArrayList<SyndFeed> list = new ArrayList<>();
        feeds.forEach((key, value) -> {
            if (!value.contains(member.getUser().getIdLong())) {
                SyndFeed feed = createFeed(key);
                feed.setUri(key);
                list.add(feed);
            }
        });
        if (list.isEmpty()) {
            return false;
        }

        SearchResult<SyndFeed> sr = new SearchResult<>(channel, member, list, (selected) -> {
            addFeed(selected.getUri(), member);
        });
        sr.setTitle("**Available Feeds**");
        sr.setLineMaker((feed) -> feed.getTitle());
        sr.start();
        return true;
    }

    public boolean searchRemove(Member member, TextChannel channel) {
        ArrayList<SyndFeed> list = new ArrayList<>();
        feeds.forEach((key, value) -> {
            if (value.contains(member.getUser().getIdLong())) {
                SyndFeed feed = createFeed(key);
                feed.setUri(key);
                list.add(feed);
            }
        });
        if (list.isEmpty()) {
            return false;
        }
        SearchResult<SyndFeed> sr = new SearchResult<>(channel, member, list, (selected) -> {
            removeFeed(selected.getUri(), member);
        });
        sr.setTitle("**Removable Feeds**");
        sr.setLineMaker((feed) -> feed.getTitle());
        sr.start();
        return true;
    }

    public boolean searchDelete(Member member, TextChannel channel) {
        ArrayList<SyndFeed> list = new ArrayList<>();
        feeds.forEach((key, value) -> {
            SyndFeed feed = createFeed(key);
            feed.setUri(key);
            list.add(feed);
        });
        if (list.isEmpty()) {
            return false;
        }
        SearchResult<SyndFeed> sr = new SearchResult<>(channel, member, list, (selected) -> {
            deleteFeed(selected.getUri());
        });
        sr.setTitle("**Deletable Feeds**");
        sr.setLineMaker((feed) -> feed.getTitle());
        sr.start();
        return true;
    }

    public Map<String, Boolean> listFeeds(Member member) {
        Map<String, Boolean> res = new HashMap();
        feeds.forEach((key, value) -> {
            res.put(createFeed(key).getTitle(), value.contains(member.getUser().getIdLong()));
        });
        return res;
    }

    private SyndFeed createFeed(String address) {
        try {
            URL url = new URL(address);
            return input.build(new XmlReader(url));
        } catch (IOException | FeedException ex) {
            Logger.getLogger(RssWrk.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
