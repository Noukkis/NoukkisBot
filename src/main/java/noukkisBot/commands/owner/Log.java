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
package noukkisBot.commands.owner;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Noukkis
 */
public class Log extends Command {
    
    private Logger logger;

    public Log() {
        this.name = "log";
        this.category = new Category("Owner-only");
        this.help = "Log something";
        this.guildOnly = false;
        this.ownerCommand = true;
        this.logger = LoggerFactory.getLogger(Log.class);
    }

    @Override
    protected void execute(CommandEvent event) {
        User auth = event.getAuthor();
        String authName = auth.getName() + "#" + auth.getDiscriminator();
        MessageChannel chan = event.getChannel();
        String chanName = chan.getName();
        chanName += (chan.getType().equals(ChannelType.TEXT)) ? " channel of "
                + event.getGuild() + " Guild" : "'s private channel";
        logger.info(authName + " logged \"" + event.getArgs() + "\" on " + chanName);
    }

}
