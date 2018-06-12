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
import net.dv8tion.jda.core.Permission;
import noukkisBot.helpers.Help;

/**
 *
 * @author Noukkis
 */
public class Poll extends Command {

    public Poll() {
        this.name = "poll";
        this.arguments = "<question> ? <choice 1> / <choice 2> / <choice 3>...";
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.help = "Create a poll up to 10 choices. If no choice are given, defaults are \"yes\" and \"no\".";
    }

    @Override
    protected void execute(CommandEvent event) {
        String question = event.getArgs();
        
        if (!question.contains("?") || question.length() <= 1) {
            question = "choose:";
        } else {
            question = question.split("\\?")[0] +  "?";
        }
        String[] choices = event.getArgs().replace(question, "").split("/");

        if (choices.length <= 10) {
            String reply = "**" + question + "**";
            if (choices.length > 1) {
                reply += "\n```Java\n";
                for (int i = 1; i <= choices.length; i++) {
                    reply += i + ". " + choices[i - 1].trim() + "\n";
                }
                event.reply(reply + "```", (msg) -> {
                    for (int i = 1; i <= choices.length; i++) {
                        msg.addReaction(Help.NUMBERS_REACTS[i]).queue();
                    }
                });
            } else {
                event.reply(reply, (msg) -> {
                    msg.addReaction(Help.EMOJI_YES).queue();
                    msg.addReaction(Help.EMOJI_NO).queue();
                });
            }

        } else {
            event.reactError();
        }
    }

}
