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

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.GuildontonManager;
import noukkisBot.wrks.ReactButtonsMaker;
import noukkisBot.wrks.ReactButtonsMaker.Blocker;

/**
 *
 * @author Noukkis
 */
public class AutoAntiRaid implements GuildontonManager.Guildonton {

    private final static long WAITING_TIME = 1000 * 60 * 1;
    private final static long ALERT_TIME = 1000 * 60 * 10;
    private final static long TOO_MUCH_MEMBERS = 3;

    private Guild guild;
    private Role staff;
    private Set<Member> newMembers;
    private boolean onAlert;
    private Timer timer;

    public AutoAntiRaid() {
        newMembers = Sets.newConcurrentHashSet();
        onAlert = false;
        staff = null;
        timer = new Timer();
    }

    @Override
    public void init(Guild guild) {
        this.guild = guild;
    }

    public Role getStaff() {
        return staff;
    }

    public void setStaff(Role staff) {
        this.staff = staff;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof GuildMemberJoinEvent) {
            GuildMemberJoinEvent e = (GuildMemberJoinEvent) event;
            if (e.getGuild().getIdLong() == guild.getIdLong()) {
                Member m = e.getMember();
                if (onAlert) {
                    ban(m);
                } else {
                    newMembers.add(m);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!onAlert) {
                                newMembers.remove(m);
                            }
                        }
                    }, WAITING_TIME);
                    if (newMembers.size() >= TOO_MUCH_MEMBERS) {
                        alert();
                    }
                }
            }
        }
    }

    private void alert() {
        boolean perm = guild.getSelfMember().hasPermission(Permission.BAN_MEMBERS);
        if (perm) {
            guild.getDefaultChannel().sendMessage("Beware " + staff.getAsMention() + ", I suspect a RAID !!!\nDo I take care of it ?").queue((msg) -> {
                Blocker blocker = Blocker.from(staff);
                ReactButtonsMaker.getInstance().add(msg, Help.YES_REACT, blocker, (event) -> {
                    alertTime(msg);
                });
                ReactButtonsMaker.getInstance().add(msg, Help.NO_REACT, blocker, (event) -> {
                    msg.delete().queue();
                });
            });
        } else {
            guild.getDefaultChannel().sendMessage("Beware " + staff.getAsMention() + ", I suspect a RAID !!!").queue();
        }
    }

    private void ban(Member member) {
        guild.getController().ban(member, 1, "Antiraid mode").queue();
    }

    private void alertTime(Message msg) {
        msg.delete().queue();
        onAlert = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onAlert = false;
            }
        }, ALERT_TIME);
        newMembers.forEach((member) -> ban(member));
    }

    public boolean isOnAlert() {
        return onAlert;
    }

    public void setOnAlert(boolean onAlert) {
        this.onAlert = onAlert;
    }

    @Override
    public void kill() {
        timer.cancel();
    }

}
