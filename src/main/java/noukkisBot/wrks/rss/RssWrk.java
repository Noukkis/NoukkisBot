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
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import noukkisBot.helpers.Help;
import noukkisBot.helpers.SearchResult;
import org.jdom.Element;

/**
 *
 * @author Noukkis
 */
public class RssWrk implements Runnable {

    private static final int SLEEP = 1000;
    private static final Map<Guild, RssWrk> INSTANCES = new ConcurrentHashMap<>();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    private final SyndFeedInput input;
    private final Map<String, SimpleEntry<SyndFeed, List<Member>>> feeds;

    private boolean running;
    private TextChannel chan;
    private Date lastFetch;

    public static RssWrk getInstance(Guild guild) {
        if (!INSTANCES.containsKey(guild)) {
            RssWrk rss = new RssWrk(guild);
            INSTANCES.put(guild, rss);
            new Thread(rss, "RSS-" + guild.getName()).start();
        }
        return INSTANCES.get(guild);
    }

    public static void killAll() {
        for (RssWrk wrk : INSTANCES.values()) {
            wrk.kill();
        }
    }

    private RssWrk(Guild guild) {
        this.chan = guild.getDefaultChannel();
        this.feeds = new ConcurrentHashMap<>();
        this.running = false;
        this.input = new SyndFeedInput();
        this.lastFetch = new Date();
    }

    public boolean isRunning() {
        return running;
    }

    public void kill() {
        running = false;
        INSTANCES.remove(chan.getGuild());
    }

    public Map<String, SimpleEntry<SyndFeed, List<Member>>> getFeeds() {
        return feeds;
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            Date now = new Date();
            fetch();
            lastFetch = now;
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException ex) {
            }
        }
    }

    public void setChan(TextChannel chan) {
        this.chan = chan;
    }

    public TextChannel getChan() {
        return chan;
    }

    public boolean addFeed(String address, Member member) {
        try {
            URL url = new URL(address);
            SyndFeed feed = input.build(new XmlReader(url));
            feed.setUri(address);
            if (feeds.containsKey(address)) {
                feeds.get(address).getValue().add(member);
            } else {
                ArrayList<Member> list = new ArrayList<>();
                list.add(member);
                feeds.put(address, new SimpleEntry<>(feed, list));
            }
            return true;
        } catch (IOException | FeedException ex) {
        }
        return false;
    }

    public boolean removeFeed(String address, Member member) {
        if (feeds.containsKey(address)) {
            boolean res = feeds.get(address).getValue().remove(member);
            if (feeds.get(address).getValue().isEmpty()) {
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
                URL url = new URL(key);
                SyndFeed feed = input.build(new XmlReader(url));
                feed.setUri(key);
                for (Object feedEntry : feed.getEntries()) {
                    SyndEntryImpl se = (SyndEntryImpl) feedEntry;
                    if (se.getPublishedDate().after(lastFetch)) {
                        writeMessageForEntry(se, value.getValue());
                    }
                }
            } catch (Exception ex) {
                Help.LOGGER.error("Error fetching " + key, ex);
            }
        });
    }

    private void writeMessageForEntry(SyndEntryImpl se, List<Member> subs) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(se.getTitle(), se.getUri())
                .setAuthor(se.getAuthor())
                .setFooter(DATE_FORMAT.format(se.getPublishedDate()), null);
        for (Element elem : (List<Element>) se.getForeignMarkup()) {
            builder.addField(elem.getName(), elem.getText(), true);
        }
        MessageBuilder msgBuilder = new MessageBuilder(builder);
        for (Member sub : subs) {
            msgBuilder.append(sub);
        }
        chan.sendMessage(msgBuilder.build()).queue();
    }

    public boolean search(Member member, TextChannel channel) {
        ArrayList<SyndFeed> list = new ArrayList<>();
        feeds.forEach((key, value) -> {
            if (!value.getValue().contains(member)) {
                list.add(value.getKey());
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
            if (value.getValue().contains(member)) {
                list.add(value.getKey());
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
        feeds.forEach((key, value) -> list.add(value.getKey()));
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

    public static HashMap<Long, HashMap<String, ArrayList<Long>>> serialize() {
        HashMap<Long, HashMap<String, ArrayList<Long>>> res = new HashMap<>();
        INSTANCES.forEach((guild, rss) -> {
            HashMap<String, ArrayList<Long>> map = new HashMap<>();
            res.put(rss.chan.getIdLong(), map);
            rss.getFeeds().forEach((feedUrl, value) -> {
                map.put(feedUrl, new ArrayList<>());
                value.getValue().forEach((member) -> {
                    map.get(feedUrl).add(member.getUser().getIdLong());
                });
            });
        });
        return res;
    }

    public static void unserialize(JDA jda, HashMap<Long, HashMap<String, ArrayList<Long>>> map) {
        map.forEach((chanID, value) -> {
            TextChannel chan = jda.getTextChannelById(chanID);
            Guild guild = chan.getGuild();
            RssWrk rss = getInstance(guild);
            rss.setChan(chan);
            value.forEach((feedUrl, members) -> {
                for (Long memberID : members) {
                    Member member = guild.getMemberById(memberID);
                    rss.addFeed(feedUrl, member);
                }
            });
        });
    }
}
