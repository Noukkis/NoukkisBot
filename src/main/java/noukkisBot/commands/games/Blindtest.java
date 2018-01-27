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
package noukkisBot.commands.games;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import noukkisBot.wrks.music.MusicWrk;

/**
 *
 * @author Noukkis
 */
public class Blindtest extends Command {

    public Blindtest() {
        this.name = "blindtest";
        this.aliases = new String[]{"bt"};
        this.help = "Permet de jouer de la musique choisie en pm";
        this.category = new Category("Games");
        this.botPermissions = new Permission[]{Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK};
    }

    @Override
    protected void execute(CommandEvent event) {
        MusicWrk wrk = MusicWrk.getInstance(event.getGuild());
        VoiceChannel chan = event.getMember().getVoiceState().getChannel();
        if (chan != null) {
            if (wrk.connect(chan)) {
                wrk.createMessageVisualPlayerForBlindTest(event, "Joined");
            } else {
                event.reactError();
            }
        } else {
            event.replyError("You're not connected to any VoiceChannel");
        }
    }

}