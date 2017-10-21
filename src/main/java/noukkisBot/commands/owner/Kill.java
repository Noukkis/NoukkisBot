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
import net.dv8tion.jda.core.JDA.Status;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.music.MusicWrk;

/**
 *
 * @author Noukkis
 */
public class Kill extends Command {

    public Kill() {
        this.name = "kill";
        this.category = new Category("Owner-only");
        this.aliases = new String[]{"k", "restart", "r"};
        this.help = "Kill or restart this bot";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        System.out.println("Killed by command");
        MusicWrk.kill();
        if (event.getMessage().getContent().contains("r")) {
            event.replySuccess("Bot will restart");
            new Thread(() -> {
                while (!event.getJDA().getStatus().equals(Status.SHUTDOWN)) {
                    System.out.print("");
                }
                System.exit(Help.KILL_STATUS);
            }).start();
        } else {
            event.replySuccess("Bot shut down");
        }
        event.getJDA().shutdown();
    }

}
