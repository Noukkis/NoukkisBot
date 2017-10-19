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
import com.jagrosh.jdautilities.commandclient.examples.AboutCommand;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import noukkisBot.wrks.ReactionButtonsMaker;
import noukkisBot.wrks.music.MusicWrk;
import org.reflections.Reflections;

/**
 *
 * @author Noukkis
 */
@UtilityClass
public class Help {

    private final String PROPS_FILE = "bot.conf";
    private final String COMMANDS_PACKAGE = "noukkisBot.commands";

    public final ReactionButtonsMaker RBM = new ReactionButtonsMaker();
    public final String YES_REACT = "\uD83D\uDC4D";
    public final String NO_REACT = "\uD83D\uDC4E";
    public final String[] NUMBERS_REACTS = {"0\u20E3", "1\u20E3", "2\u20E3",
        "3\u20E3", "4\u20E3", "5\u20E3", "6\u20E3", "7\u20E3", "8\u20E3",
        "9\u20E3", "\uD83D\uDD1F"};

    public String BOT_TOKEN;
    public String OWNER_ID;

    public void init() throws IOException {
        AudioSourceManagers.registerRemoteSources(MusicWrk.APM);
        Properties props = new Properties();
        props.load(new FileReader(PROPS_FILE));
        BOT_TOKEN = props.getProperty("token");
        OWNER_ID = props.getProperty("owner");
    }

    public void setCommands(CommandClientBuilder ccb) {
        Reflections r = new Reflections(COMMANDS_PACKAGE);
        Set<Permission> perms = new HashSet<>();
        for (Class<? extends Command> c : r.getSubTypesOf(Command.class)) {
            try {
                Command command = c.newInstance();
                ccb.addCommand(command);
                perms.addAll(Arrays.asList(command.getBotPermissions()));
            } catch (InstantiationException | IllegalAccessException ex) {
                System.err.println("Can't instance " + c.getName());
                ex.printStackTrace();
            }
        }
        String[] features = {"Clean chats", "Polls maker", "Music"};
        ccb.addCommand(new AboutCommand(Color.YELLOW, "a cool bot", features, perms.toArray(new Permission[0])));
    }

    public static void setHelp(CommandClientBuilder ccb) {
        ccb.setHelpFunction((event) -> {
            CommandClient cc = event.getClient();
            String help = "**" + event.getSelfUser().getName() + "** commands:";
            String prefix = cc.getPrefix();
            Map<String, List<Command>> commands = new LinkedHashMap<>();
            List<Command> others = new ArrayList<>();
            for (Command command : cc.getCommands()) {
                if (!(command.isOwnerCommand() && !cc.getOwnerId().equals(event.getAuthor().getId()))) {
                    Category category = command.getCategory();
                    if (category != null) {
                        if (!commands.containsKey(category.getName())) {
                            commands.put(category.getName(), new ArrayList<>());
                        }
                        commands.get(category.getName()).add(command);
                    } else {
                        others.add(command);
                    }
                }
            }
            commands.put("Others", others);
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
}
