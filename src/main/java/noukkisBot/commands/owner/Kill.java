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
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.GuildontonManager;
import noukkisBot.wrks.contest.ContestWrk;
import noukkisBot.wrks.music.MusicWrk;

/**
 *
 * @author Noukkis
 */
public class Kill extends Command {

    public Kill() {
        this.name = "kill";
        this.category = new Category("Owner-only");
        this.aliases = new String[]{"s", "restart", "r"};
        this.help = "Kill or restart this bot";
        this.guildOnly = false;
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        Help.LOGGER.info("Killed by command");
        Help.BACKUP.stop();
        Help.BACKUP.backupNow();
        MusicWrk.killAll();
        ContestWrk.killAll();
        GuildontonManager.getInstance().killAll();
        boolean restart = event.getMessage().getContentRaw().contains("r");
        event.replySuccess(restart ? "Bot will restart" : "Bot shut down");
        if (restart) {
            event.getJDA().addEventListener((EventListener) (Event e) -> {
                if (e instanceof ShutdownEvent) {
                    System.exit(Help.RESTART_STATUS);
                }
            });
        }
        event.getJDA().shutdown();
    }

}
