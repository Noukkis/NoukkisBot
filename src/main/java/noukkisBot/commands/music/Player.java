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
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;
import noukkisBot.wrks.music.MusicWrk;

/**
 *
 * @author Noukkis
 */
public class Player extends Command {

    private static MessageEmbed help() {
        return new EmbedBuilder().setTitle("VisualPlayer")
                .setDescription("How to use it")
                .addField("Reaction Buttons", "Click on these reactions to performs actions (or use the alternative command)"
                        + "```Java\n"
                        + "⏹ (\"stop\") : Stop the song and clear the queue\n"
                        + "⏯ (\"pause\") : Pause / Continue the track\n"
                        + "⏭ (\"next\") : Launch the next track\n"
                        + "🔀 (\"shuffle\") : Shuffle the queue\n"
                        + "```", true)
                .addBlankField(true)
                .addField("player <args> command",
                        "`c` - put the classical VisualPlayer in this channel\n"
                        + "`x` - delete the classical VisualPlayer\n"
                        + "`t` - put the in-topic VisualPlayer in this channel or delete it if it's already there", true)
                .build();
    }

    public Player() {
        this.name = "player";
        this.arguments = "[c][t][x]";
        this.help = "manage the visual music player";
        this.botPermissions = new Permission[]{Permission.MANAGE_CHANNEL};
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event) {
        MusicWrk wrk = MusicWrk.INSTANCES.get(event.getGuild());
        if (event.getArgs().matches("[c|t|x]+") && wrk != null) {
            if (event.getArgs().contains("c")) {
                wrk.createMessageVisualPlayer(event, "Created");
            }
            if (event.getArgs().contains("t")) {
                wrk.topicVisualPlayer(event);
            }
            if (event.getArgs().contains("x")) {
                wrk.deleteMessageVisualPlayer();
            }
            event.getMessage().delete().queue();
        } else {
            event.reply(help());
        }
    }

}
