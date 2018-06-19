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
package noukkisBot.helpers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.core.entities.Guild;
import static noukkisBot.commands.moderation.AutoRole.setupMsg;
import noukkisBot.wrks.GuildontonManager.Guildonton;

/**
 *
 * @author Noukkis
 */
public class GuildValues implements Guildonton {

    private final Map<String, Long> autoRole;
    private final Map<String, String> values;

    private transient Guild guild;

    @Override
    public void kill() {
    }

    public GuildValues() {
        autoRole = new ConcurrentHashMap<>();
        values = new ConcurrentHashMap<>();
    }

    public Map<String, Long> getAutoRole() {
        return autoRole;
    }

    public String getValue(String key) {
        return values.get(key);
    }

    public void setValue(String key, String value) {
        values.put(key, value);
    }

    @Override
    public void init(Guild guild) {
        this.guild = guild;
        initAutoRole();
    }

    private void initAutoRole() {
        String msgID = getValue("autoRoleMsg");
        if (msgID != null) {
            setupMsg(Help.msgID(guild, msgID), this);
        }
    }

}
