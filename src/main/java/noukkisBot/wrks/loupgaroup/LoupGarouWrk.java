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
package noukkisBot.wrks.loupgaroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.VoiceChannel;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.ReactButtonsMaker;
import noukkisBot.wrks.RestActionScheduler;

/**
 *
 * @author Noukkis
 */
public class LoupGarouWrk {

    private static final Map<Guild, LoupGarouWrk> INSTANCES = new HashMap<>();

    public enum Role {
        LOUP, CHASSEUR, SORCIERE, VOYANTE, VILLAGEOIS
    };

    private final VoiceChannel chan;
    private final Map<Member, Role> members;
    private final ArrayList<Role> roles;
    private final Message configMsg;
    private final RestActionScheduler<Message> configRas;
    private final RestActionScheduler<Message> ranksRas;

    public static LoupGarouWrk getInstance(Guild guild) {
        return INSTANCES.get(guild);
    }

    public static LoupGarouWrk getInstance(Message msg, VoiceChannel chan) {
        INSTANCES.putIfAbsent(chan.getGuild(), new LoupGarouWrk(msg, chan));
        return INSTANCES.get(chan.getGuild());
    }

    private LoupGarouWrk(Message msg, VoiceChannel chan) {
        this.chan = chan;
        this.configMsg = msg;
        this.members = new HashMap<>();
        this.roles = new ArrayList<>();
        this.configRas = new RestActionScheduler<>(1000);
        this.ranksRas = new RestActionScheduler<>(1000);
    }

    public void stop() {

    }

    public void init() {
        RestActionScheduler ras = new RestActionScheduler(1000);
        ReactButtonsMaker rms = ReactButtonsMaker.getInstance();
        rms.add(configMsg, Help.NUMBERS_REACTS[1], (event) -> {roles.add(Role.LOUP); listRoles(ras, configMsg);});
        rms.add(configMsg, Help.NUMBERS_REACTS[2], (event) -> {roles.add(Role.CHASSEUR); listRoles(ras, configMsg);});
        rms.add(configMsg, Help.NUMBERS_REACTS[3], (event) -> {roles.add(Role.SORCIERE); listRoles(ras, configMsg);});
        rms.add(configMsg, Help.NUMBERS_REACTS[4], (event) -> {roles.add(Role.VOYANTE); listRoles(ras, configMsg);});
        rms.add(configMsg, Help.NUMBERS_REACTS[5], (event) -> {roles.add(Role.VILLAGEOIS); listRoles(ras, configMsg);});
        rms.add(configMsg, "✅", (event) -> start());
        listRoles(ras, configMsg);
    }

    private void listRoles(RestActionScheduler ras, Message msg) {
        String s = "```\n";
        int i = 1;
        for (Role role : Role.values()) {
            s += i + ". " + role + " [" + Collections.frequency(roles, role) + "]\n";
            i++;
        }
        ras.schedule(msg.editMessage(s + "```"), false);
    }

    private void start() {
        configMsg.delete().queue();
    }
}
