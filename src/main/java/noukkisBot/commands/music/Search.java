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
package noukkisBot.commands.music;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import noukkisBot.wrks.music.MusicWrk;

/**
 *
 * @author Noukkis
 */
public class Search extends Command {

    public Search() {
        this.name = "search";
        this.arguments = "[-s] <song name>";
        this.help = "search a song on YouTube (or SoundCloud with the -s argument)";
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event) {
        MusicWrk wrk = MusicWrk.getInstance(event.getGuild());
        if(event.getArgs().startsWith("-s ")) {
             wrk.searchMusic(event, "scsearch:", event.getArgs().replaceFirst("-s ", ""));
        } else {
            wrk.searchMusic(event, "ytsearch:", event.getArgs());
        }
       
    }

}
