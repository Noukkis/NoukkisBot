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
package noukkisBot.wrks.link;

import java.util.HashMap;
import net.dv8tion.jda.core.entities.Guild;

/**
 *
 * @author Noukkis
 */
public class LinkWrk {

    private static final HashMap<String, LinkWrk> instances = new HashMap<>();

    private final HashMap<Guild, AudioLinkHandler> guilds;
    
    public static LinkWrk getInstance(String key) {
        if(!instances.containsKey(key)) {
            instances.put(key, new LinkWrk());
        }
        return instances.get(key);
    }

    private LinkWrk() {
        this.guilds = new HashMap<>();
    }

    public static void removeGuild(Guild guild) {
        for (LinkWrk wrk : instances.values()) {
            if (wrk.guilds.containsKey(guild)) {
                guild.getAudioManager().closeAudioConnection();
                wrk.guilds.remove(guild);
            }
        }
    }

    public void addGuild(Guild guild) {
        AudioLinkHandler alh = new AudioLinkHandler(guild, this);
        guilds.put(guild, alh);
        guild.getAudioManager().setReceivingHandler(alh);
        guild.getAudioManager().setSendingHandler(alh);
    }

    public void receive(byte[] audioData, Guild guild) {
        for (Guild g : guilds.keySet()) {
            if (g.getIdLong() != guild.getIdLong()) {
                guilds.get(g).receive(audioData);
            }
        }
    }

}
