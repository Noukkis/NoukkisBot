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
package noukkisBot.commands.contests;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import noukkisBot.wrks.contest.ContestWrk;

/**
 *
 * @author Noukkis
 */
public class Contest extends Command {

    public Contest() {
        this.name = "contest";
        this.help = "Create a contest between the members of your Voice Channel";
        this.category = new Category("Contest");
        this.botPermissions = new Permission[]{Permission.NICKNAME_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        VoiceChannel chan = event.getMember().getVoiceState().getChannel();
        if (chan != null) {
            event.reply("Do you participate to this contest ?", (msg) -> ContestWrk.getInstance(msg, chan).start());
        } else {
            event.replyError("You're not connected to any VoiceChannel");
        }
    }

}
