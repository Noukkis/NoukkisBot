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
package noukkisBot.helpers;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.Command.Category;
import com.jagrosh.jdautilities.commandclient.CommandClient;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import noukkisBot.wrks.auto.AutoBackup;
import noukkisBot.wrks.music.MusicWrk;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Noukkis
 */
public class Help {

    private final static String PROPS_FILE = "bot.conf";
    private final static String COMMANDS_PACKAGE = "noukkisBot.commands";
    private final static Properties PROPS = new Properties();

    public final static Logger LOGGER = LoggerFactory.getLogger(Help.class);
    public final static File BACKUP_FILE = new File("data.bak");
    public final static int RESTART_STATUS = 1;
    public final static String EMOJI_YES = "\uD83D\uDC4D";
    public final static String EMOJI_NO = "\uD83D\uDC4E";
    public final static String EMOJI_SUCCESS = "\u2705";
    public final static String EMOJI_WARNING = "\u26A0";
    public final static String EMOJI_ERROR = "\u274C";
    public final static String[] NUMBERS_REACTS = {"0\u20E3", "1\u20E3", "2\u20E3",
        "3\u20E3", "4\u20E3", "5\u20E3", "6\u20E3", "7\u20E3", "8\u20E3",
        "9\u20E3", "\uD83D\uDD1F"};

    public static String BOT_TOKEN;
    public static String OWNER_ID;
    public static AutoBackup BACKUP;

    public static void init() throws IOException {
        LOGGER.info("Starting Bot using Java " + System.getProperty("java.version"));
        AudioSourceManagers.registerRemoteSources(MusicWrk.APM);
        PROPS.load(new FileReader(PROPS_FILE));
        BOT_TOKEN = PROPS.getProperty("token");
        OWNER_ID = PROPS.getProperty("owner");
    }

    public static void deleteIn(Message msg, long time) {
        if (msg.getChannelType() == ChannelType.TEXT) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    msg.delete().queue();
                }
            }, time);
        }
    }

    public static void setCommands(CommandClientBuilder ccb) {
        Reflections r = new Reflections(COMMANDS_PACKAGE);
        Set<Permission> perms = new HashSet<>();
        for (Class<? extends Command> c : r.getSubTypesOf(Command.class)) {
            try {
                Command command = c.newInstance();
                ccb.addCommand(command);
                perms.addAll(Arrays.asList(command.getBotPermissions()));
            } catch (InstantiationException | IllegalAccessException ex) {
                LOGGER.error("Can't instance " + c.getName(), ex);
            }
        }
        String[] features = {"Cleans Chat", "Makes Polls", "Plays Music", "Manages Contests",
                            "Fetches RSS feeds", "Some others things"};
        String description = "a cool [Open Source](https://github.com/Noukkis/NoukkisBot) bot";
        ccb.addCommand(new AboutCommand(Color.YELLOW, description, features, perms.toArray(new Permission[0])));
    }

    public static Map<String, List<Command>> getCommands(CommandEvent event) {
        Map<String, List<Command>> res = new LinkedHashMap<>();
        CommandClient cc = event.getClient();
        List<Command> others = new ArrayList<>();
        for (Command command : cc.getCommands()) {
            if (!(command.isOwnerCommand() && !cc.getOwnerId().equals(event.getAuthor().getId()))) {
                Category category = command.getCategory();
                if (category != null) {
                    if (!res.containsKey(category.getName())) {
                        res.put(category.getName(), new ArrayList<>());
                    }
                    res.get(category.getName()).add(command);
                } else {
                    others.add(command);
                }
            }
        }
        res.put("Others", others);
        return res;
    }

    public static void setHelp(CommandClientBuilder ccb) {
        ccb.setHelpFunction((event) -> {
            CommandClient cc = event.getClient();
            String help = "**" + event.getSelfUser().getName() + "** commands:";
            String prefix = cc.getPrefix();
            Map<String, List<Command>> commands = getCommands(event);
            for (String category : commands.keySet()) {
                help += "\n\n__" + category + "__\n";
                Collections.sort(commands.get(category), (o1, o2) -> {
                    return o1.getName().compareTo(o2.getName());
                });
                for (Command command : commands.get(category)) {
                    help += "\n`" + prefix + command.getName()
                            + (command.getArguments() != null ? " " + command.getArguments() : "")
                            + "` - " + command.getHelp();
                }
            }

            User owner = event.getJDA().getUserById(cc.getOwnerId());
            if (owner != null) {
                help += "\n\nFor additional help, contact **" + owner.getName() + "**#" + owner.getDiscriminator();
                if (cc.getServerInvite() != null) {
                    help += " or join " + cc.getServerInvite();
                }
            }
            return help;
        });
    }

    public static JsonObject httpJsonGet(String baseUrl, String query) throws MalformedURLException {
        try {
            HttpURLConnection connection = null;
            URL url = new URL(baseUrl + URLEncoder.encode(query, "UTF-8"));
            try (InputStream is = url.openStream(); JsonReader rdr = Json.createReader(is)) {
                return rdr.readObject();
            } catch (IOException ex) {
                return null;
            }
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    public static String httpget(String url) {
        InputStream is = null;
        try {
            is = new URL(url).openStream();
            try {

                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                return readAll(rd);
            } finally {
                is.close();
            }
        } catch (IOException ex) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
            }
        }
        return null;
    }

    public static String readAll(BufferedReader rd) {
        try {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            return sb.toString();
        } catch (IOException ex) {
        }
        return null;
    }

    public static String getProp(String key) {
        return PROPS.getProperty(key);
    }

    public static String encodeURL(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    
    public static void sendToOwner(String msg, JDA jda) {
        jda.getUserById(OWNER_ID).openPrivateChannel().queue((chan) -> {
            chan.sendMessage(msg).queue();
        });
    }
}
