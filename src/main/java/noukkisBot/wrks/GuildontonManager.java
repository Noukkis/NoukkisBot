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
package noukkisBot.wrks;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.gson.GsonBuilder;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import noukkisBot.helpers.Help;

/**
 *
 * @author Noukkis
 */
public class GuildontonManager implements EventListener {

    public static boolean unserialize(JDA jda, Serializable serial) {
        if (serial instanceof HashMap) {
            HashMap<Long, ClassToInstanceMap<Guildonton>> serialMap = (HashMap<Long, ClassToInstanceMap<Guildonton>>) serial;
            serialMap.forEach((guildId, ctim) -> {
                Guild guild = jda.getGuildById(guildId);
                if (guild != null) {
                    INSTANCE.map.put(guildId, ctim);
                    ctim.forEach((c, guildonton) -> {
                        guildonton.init(guild);
                    });
                }
            });
            return true;
        }
        return false;
    }

    private HashMap<Long, ClassToInstanceMap<Guildonton>> map;

    private GuildontonManager() {
        map = new HashMap<>();
    }

    public <G extends Guildonton> G getGuildonton(Guild guild, Class<G> c) {
        map.putIfAbsent(guild.getIdLong(), MutableClassToInstanceMap.create());
        ClassToInstanceMap<Guildonton> ctim = map.get(guild.getIdLong());
        if (!ctim.containsKey(c)) {
            try {
                G instance = c.newInstance();
                instance.init(guild);
                ctim.putInstance(c, instance);
            } catch (InstantiationException | IllegalAccessException ex) {
                Help.LOGGER.error("Can't instance Guildonton " + c.getName(), ex);
                return null;
            }
        }
        return ctim.getInstance(c);
    }

    @Override
    public void onEvent(Event event) {
        map.forEach((guild, ctim) -> {
            ctim.forEach((c, instance) -> {
                instance.onEvent(event);
            });
        });
    }

    public void killAll() {
        map.forEach((guildId, ctim) -> {
            new HashSet<>(ctim.keySet()).forEach((c) -> {
                kill(guildId, c);
            });
        });
    }

    public void kill(Long guildId, Class<? extends Guildonton> c) {
        if (map.containsKey(guildId)) {
            ClassToInstanceMap<Guildonton> ctim = map.get(guildId);
            if (ctim.containsKey(c)) {
                ctim.get(c).kill();
            }
            ctim.remove(c);
        }
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson(map);
    }

    public HashMap<Long, ClassToInstanceMap<Guildonton>> getMap() {
        return map;
    }

    public interface Guildonton extends Serializable {

        void init(Guild guild);

        default void onEvent(Event event) {
        }

        void kill();

    }

    private static final GuildontonManager INSTANCE = new GuildontonManager();

    public static GuildontonManager getInstance() {
        return INSTANCE;
    }
}
