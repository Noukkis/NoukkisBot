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
package noukkisBot.wrks.auto;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.GuildontonManager.Guildonton;
import noukkisBot.wrks.ReactButtonsMaker;

/**
 *
 * @author Noukkis
 */
public class AutoSas implements Guildonton {
    
    private long roleID;
    private long chanID;
    private transient Guild guild;
    private transient ReactButtonsMaker rbm;

    @Override
    public void init(Guild guild) {
        this.guild = guild;
        this.rbm = ReactButtonsMaker.getInstance();
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof GuildMemberJoinEvent) {
            GuildMemberJoinEvent e = (GuildMemberJoinEvent) event;
            if(e.getGuild().getIdLong() == guild.getIdLong()) {
                memberRequest(e.getMember());
            }
        }
    }

    @Override
    public void kill() {
    }
    
    public void setRole(Role role) {
        this.roleID = role.getIdLong();
    }
    
    public Role getRole() {
        return guild.getRoleById(roleID);
    }    
    
    public void setChan(TextChannel chan) {
        this.chanID = chan.getIdLong();
    }
    
    public TextChannel getChan() {
        return guild.getTextChannelById(chanID);
    }

    private void memberRequest(Member member) {
        getChan().sendMessage(member.getAsMention() + " wants to join !").queue((msg) -> {
            rbm.add(msg, Help.YES_REACT, (react) -> {
                guild.getController().addRolesToMember(member, getRole()).queue();
                msg.delete().queue();
            });
            User user = member.getUser();
            rbm.add(msg, Help.NO_REACT, (react) -> {
                guild.getController().ban(member, 2).queue((v) -> {
                    guild.getController().unban(user).queue();
                    msg.delete().queue();
                });
            });
        });
    }
}
