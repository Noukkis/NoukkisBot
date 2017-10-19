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

/**
 *
 * @author Noukkis
 */
public class CodeMaker extends Command {

    public CodeMaker() {
        this.name = "code";
        this.aliases = new String[]{"c"};
        this.arguments = "<language> <code>";
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.help = "wrap your message in a code block";
    }

    @Override
    protected void execute(CommandEvent ce) {
        String author = ce.getAuthor().getName();
        String language = ce.getArgs().split("\\s+")[0];
        String code = ce.getArgs().replaceFirst(language, "").replaceAll("`", "\u00AD`");
        String msg = "**" + author + "**'s " + language + " code :\n```" + language + "\n" + code + "\n```";
        ce.getTextChannel().sendMessage(msg).queue();
        ce.getMessage().delete().queue();
    }

}
