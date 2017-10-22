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
import java.util.LinkedList;
import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Noukkis
 */
public class Clear extends Command {

    public Clear() {
        this.name = "clear";
        this.aliases = new String[]{"cl"};
        this.arguments = "<number>";
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.userPermissions = botPermissions;
        this.help = "clear the channel from its <number> last messages (default 100)";
    }

    @Override
    protected void execute(CommandEvent event) {
        MessageHistory h = event.getChannel().getHistory();
        List<Message> msgs = null;
        if (event.getArgs().trim().isEmpty()) {
            msgs = retrieveMessages(100, h);
        } else {
            try {
                int x = Integer.parseInt(event.getArgs()) + 1;
                if (x > 1) {
                    msgs = retrieveMessages(x, h);
                }
            } catch (Exception e) {
            }
        }

        if (msgs != null) {
            if (msgs.size() == 2) {
                event.getTextChannel().deleteMessages(msgs).queue();
                event.replySuccess("1 message deleted");
            } else {
                delete(event.getTextChannel(), msgs);
                event.replySuccess((msgs.size() - 1) + " messages deleted");
            }

        } else {
            event.replyError("argument must be a number bigger than 0");
        }
    }

    private List<Message> retrieveMessages(int x, MessageHistory h) {
        List<Message> msgs = new LinkedList<>();
        if (x < 1) {
            return msgs;
        }
        if (x < 100) {
            msgs.addAll(h.retrievePast(x).complete());
        } else {
            msgs.addAll(h.retrievePast(100).complete());
            msgs.addAll(retrieveMessages(x - 100, h));
        }
        return msgs;
    }

    private void delete(TextChannel chan, List<Message> msgs) {
        if(msgs.size() <= 100) {
            chan.deleteMessages(msgs).queue();
        } else {
            chan.deleteMessages(msgs.subList(0, 100)).queue();
            delete(chan, msgs.subList(100, msgs.size()));
        }
    }
}
