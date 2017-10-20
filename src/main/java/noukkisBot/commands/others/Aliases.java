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
package noukkisBot.commands.others;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import noukkisBot.helpers.Help;

/**
 *
 * @author Noukkis
 */
public class Aliases extends Command {

    public Aliases() {
        this.name = "aliases";
        this.aliases = new String[]{"alias"};
        this.guildOnly = false;
        this.help = "Check the aliases of each command";
    }

    @Override
    protected void execute(CommandEvent event) {
        String aliases = "**" + event.getSelfUser().getName() + "** commands aliases:";
        Map<String, List<Command>> commands = Help.getCommands(event);
        String prefix = event.getClient().getPrefix();
        for (String category : commands.keySet()) {
            String categoryString = "\n\n__" + category + "__\n";
            boolean empty = true;
            Collections.sort(commands.get(category), (o1, o2) -> {
                return o1.getName().compareTo(o2.getName());
            });
            for (Command command : commands.get(category)) {
                if (command.getAliases() != null && command.getAliases().length > 0) {
                    empty = false;
                    categoryString += "\n" + prefix + command.getName() + " :";
                    for (String aliase : command.getAliases()) {
                        categoryString += " `" + aliase + "`";
                    }
                }
            }
            if(!empty) {
                aliases += categoryString;
            }
        }
        event.reactSuccess();
        event.replyInDM(aliases);
    }
}
