/*
 * The MIT License
 *
 * Copyright 2018 Noukkis.
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
package noukkisBot.commands.moderation;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import java.util.function.Consumer;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import noukkisBot.helpers.GuildValues;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.GuildontonManager;
import noukkisBot.wrks.ReactButtonsMaker;

/**
 *
 * @author Noukkis
 */
public class AutoRole extends Command {

    public AutoRole() {
        this.name = "autorole";
        this.help = "Assign/Unassign automatically a role by an emote";
        this.arguments = "msg | list | <emote> <@role>";
        this.category = new Category("Moderation");
        this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(CommandEvent event) {
        GuildValues val = GuildontonManager.getInstance().getGuildonton(event.getGuild(), GuildValues.class);
        switch (event.getArgs()) {
            case "msg": createMsg(event, val);
                break;
            case "list": event.reply(list(event, val).build());
                break;
            default: create(event, val);
                break;
        }
    }

    private void createMsg(CommandEvent event, GuildValues val) {
        event.getChannel().sendMessage(list(event, val).build()).queue((msg) -> {
            val.getAutoRole().forEach((emote, roleID) -> {
                Consumer<GenericMessageReactionEvent> consumer = (emoteEvent) -> {
                    Member member = emoteEvent.getMember();
                    Role role = event.getGuild().getRoleById(roleID);
                    if (emoteEvent instanceof MessageReactionAddEvent) {
                        event.getGuild().getController().addSingleRoleToMember(member, role).queue();
                    } else {
                        event.getGuild().getController().removeSingleRoleFromMember(member, role).queue();
                    }
                };
                if (!event.getGuild().getEmotesByName(emote.replace(":", ""), true).isEmpty()) {
                    Emote em = event.getGuild().getEmotesByName(emote.replace(":", ""), true).get(0);
                    ReactButtonsMaker.getInstance().add(msg, em, consumer);
                } else {
                    ReactButtonsMaker.getInstance().add(msg, emote, consumer);
                }
            });
        });
    }

    private MessageBuilder list(CommandEvent event, GuildValues val) {
        MessageBuilder msg = new MessageBuilder("AutoRole List :\n-----------------");
        val.getAutoRole().forEach((emote, roleID) -> {
            if (!event.getGuild().getEmotesByName(emote.replace(":", ""), true).isEmpty()) {
                emote = event.getGuild().getEmotesByName(emote.replace(":", ""), true).get(0).getAsMention();
            }
            msg.append("\n\n").append(emote).append(" => ")
                    .append(event.getGuild().getRoleById(roleID));
        });
        return msg;
    }

    private void create(CommandEvent event, GuildValues val) {
        String[] args = event.getMessage().getContentDisplay().split(" ");
        boolean ok = false;
        if (args.length >= 2 && event.getMessage().getMentionedRoles().size() == 1) {
            String emote = args[1];
            Role role = event.getMessage().getMentionedRoles().get(0);
            String realEmote = Help.getReaction(emote, event.getMessage());
            if (realEmote != null) {
                val.getAutoRole().put(emote, role.getIdLong());
                ok = true;
            }
        } else if (args.length >= 2) {
            String emote = args[1];
            if (val.getAutoRole().containsKey(emote)) {
                val.getAutoRole().remove(emote);
                ok = true;
            }
        }
        if (ok) {
            event.reactSuccess();
        } else {
            event.reactError();
        }
    }

}
