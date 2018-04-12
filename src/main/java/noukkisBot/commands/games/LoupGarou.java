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

/**
 *
 * @author Noukkis
 */
public class LoupGarou extends Command {

    public LoupGarou() {
        this.name = "loupgarou";
        this.aliases = new String[]{"lg"};
        this.help = "Commencer une partie de loup garou";
        this.category = new Category("Games");
        this.botPermissions = new Permission[]{Permission.VOICE_CONNECT,
            Permission.VOICE_SPEAK, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS};
    }

    @Override
    protected void execute(CommandEvent event) {
//        if (LoupGarouWrk.getInstance(event.getGuild()) != null) {
//            LoupGarouWrk.getInstance(event.getGuild()).stop();
//        }
//        GuildVoiceState gvs = event.getMember().getVoiceState();
//        if (gvs.inVoiceChannel()) {
//            event.reply("Création du jeu...", (msg) -> {
//                LoupGarouWrk.getInstance(msg, gvs.getChannel()).init();
//            });
//        }
        event.replyWarning("Fonctionnalitée non-implémentée");
    }

}
