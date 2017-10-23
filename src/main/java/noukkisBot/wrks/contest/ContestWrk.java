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
package noukkisBot.wrks.contest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.ReactButtonsMaker;

/**
 *
 * @author Noukkis
 */
public class ContestWrk {

    private static final Map<Guild, ContestWrk> INSTANCES = new HashMap<>();
    private static final ReactButtonsMaker rbm = ReactButtonsMaker.getInstance();

    private final VoiceChannel chan;
    private final Map<Member, Integer> members;

    private ArrayList<Message> msgs;

    public static ContestWrk getInstance(Guild guild) {
        return INSTANCES.get(guild);
    }

    public static ContestWrk getInstance(Message msg, VoiceChannel chan) {
        if (!INSTANCES.containsKey(chan.getGuild())) {
            INSTANCES.put(chan.getGuild(), new ContestWrk(msg, chan));
        }
        return INSTANCES.get(chan.getGuild());
    }

    public static void killAll() {
        for (ContestWrk wrk : INSTANCES.values()) {
            wrk.kill();
        }
    }

    private ContestWrk(Message msg, VoiceChannel chan) {
        this.chan = chan;
        this.msgs = new ArrayList<>();
        msgs.add(msg);
        this.members = new HashMap<>();
    }

    public void start() {
        rbm.add(msgs.get(0), Help.YES_REACT, (event) -> start(true));
        rbm.add(msgs.get(0), Help.NO_REACT, (event) -> start(false));
    }

    private void start(boolean participate) {
        rbm.removeAll(msgs.get(0));
        
        Message msg = msgs.get(0);
        String s = "```Markdown";
        int i = 1;
        for (Member member : chan.getMembers()) {
            members.put(member, 0);
            if(i > 10) {
                i = 1;
                msg.editMessage(s + "```").queue();
                msg = msg.getTextChannel().sendMessage("").complete();
                msgs.add(msg);
                s = "```Markdown";
            }
            rbm.add(msg, Help.NUMBERS_REACTS[i], (event) -> addPoint(member));
            s += "\n" + i + ". " + member.getEffectiveName();
            i++;
        }
        msg.editMessage(s + "```").queue();
    }

    private void kill() {
        for (Message msg : msgs) {
            msg.delete().queue();
        }
    }

    private void addPoint(Member member) {
        int p = members.get(member) + 1;
        members.replace(member, p);
    }
}
