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

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import java.util.HashMap;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import noukkisBot.helpers.Help;

/**
 *
 * @author Noukkis
 */
public class GuildontonManager implements EventListener {

    private HashMap<Guild, ClassToInstanceMap<Guildonton>> map;

    private GuildontonManager() {
        map = new HashMap<>();
    }

    public <G extends Guildonton> G getGuildonton(Guild guild, Class<G> c) {
        map.putIfAbsent(guild, MutableClassToInstanceMap.create());
        ClassToInstanceMap<Guildonton> ctim = map.get(guild);
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
        map.forEach((guild, ctim) -> {
            ctim.forEach((c, instance) -> {
                kill(guild, c);
            });
        });
    }

    public void kill(Guild guild, Class<? extends Guildonton> c) {
        if(map.containsKey(guild)) {
            ClassToInstanceMap<Guildonton> ctim = map.get(guild);
            if(ctim.containsKey(c)) {
                ctim.get(c).kill();
            }
            ctim.remove(c);
        }
    }

    public interface Guildonton {

        void init(Guild guild);

        void onEvent(Event event);

        void kill();
    }

    private final static GuildontonManager INSTANCE = new GuildontonManager();

    public static GuildontonManager getInstance() {
        return INSTANCE;
    }
}
