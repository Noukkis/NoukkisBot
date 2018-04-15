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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import noukkisBot.helpers.SearchResult;
import org.jdom.Element;

/**
 *
 * @author Noukkis
 */
public class RssWrk implements Runnable {

    private static final int SLEEP = 1000;
    private static final Map<Guild, RssWrk> INSTANCES = new HashMap<>();
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private boolean running;
    private TextChannel chan;
    private SyndFeedInput input;
    private Date lastFetch;
    private HashMap<String, Pair<SyndFeed, ArrayList<Member>>> feeds;

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
        this.feeds = new HashMap<>();
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

    public HashMap<String, Pair<SyndFeed, ArrayList<Member>>> getFeeds() {
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
                feeds.put(address, new Pair<>(feed, list));
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
        HashMap<String, Pair<SyndFeed, ArrayList<Member>>> temp = new HashMap<>(feeds);
        for (String address : temp.keySet()) {
            try {
                URL url = new URL(address);
                SyndFeed feed = input.build(new XmlReader(url));
                feed.setUri(address);
                for (Object entry : feed.getEntries()) {
                    SyndEntryImpl se = (SyndEntryImpl) entry;
                    if (se.getPublishedDate().after(lastFetch)) {
                        writeMessageForEntry(se, temp.get(address).getValue());
                    }
                }
            } catch (IOException | FeedException ex) {
                Logger.getLogger(RssWrk.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void writeMessageForEntry(SyndEntryImpl se, ArrayList<Member> subs) {
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

        SearchResult<SyndFeed> sr = new SearchResult<>(chan, member, list, (selected) -> {
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
        SearchResult<SyndFeed> sr = new SearchResult<>(chan, member, list, (selected) -> {
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
        SearchResult<SyndFeed> sr = new SearchResult<>(chan, member, list, (selected) -> {
            deleteFeed(selected.getUri());
        });
        sr.setTitle("**Deletable Feeds**");
        sr.setLineMaker((feed) -> feed.getTitle());
        sr.start();
        return true;
    }

    public static HashMap<Long, HashMap<String, ArrayList<Long>>> serialize() {
        HashMap<Long, HashMap<String, ArrayList<Long>>> res = new HashMap<>();
        for (Guild guild : INSTANCES.keySet()) {
            HashMap<String, ArrayList<Long>> map = new HashMap<>();
            res.put(guild.getIdLong(), map);
            RssWrk rss = INSTANCES.get(guild);
            for (String feedUrl : rss.getFeeds().keySet()) {
                map.put(feedUrl, new ArrayList<>());
                for (Member member : rss.getFeeds().get(feedUrl).getValue()) {
                    map.get(feedUrl).add(member.getUser().getIdLong());
                }
            }
        }
        return res;
    }
    
    public static void unserialize(JDA jda, HashMap<Long, HashMap<String, ArrayList<Long>>> map) {
        for (Long guildID : map.keySet()) {
            Guild guild = jda.getGuildById(guildID);
            RssWrk rss = getInstance(guild);
            for (String feedUrl : map.get(guildID).keySet()) {
                for (Long memberID : map.get(guildID).get(feedUrl)) {
                    Member member = guild.getMemberById(memberID);
                    rss.addFeed(feedUrl, member);
                }
            }
        }
    }
}
