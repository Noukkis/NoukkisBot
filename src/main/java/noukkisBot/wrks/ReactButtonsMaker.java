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
package noukkisBot.wrks;

import java.util.HashMap;
import java.util.function.Consumer;
import javafx.util.Pair;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageDeleteEvent;
import net.dv8tion.jda.core.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 *
 * @author Noukkis
 */
public final class ReactButtonsMaker implements EventListener {

    private static final ReactButtonsMaker INSTANCE = new ReactButtonsMaker();

    private final HashMap<String, HashMap<String, Pair<User, Consumer<GenericMessageReactionEvent>>>> map;

    public static ReactButtonsMaker getInstance() {
        return INSTANCE;
    }

    private ReactButtonsMaker() {
        map = new HashMap<>();
    }

    public void add(Message msg, String name, User blocker, Consumer<GenericMessageReactionEvent> consumer) {
        msg.addReaction(name).queue();
        if (!map.containsKey(msg.getId())) {
            map.put(msg.getId(), new HashMap<>());
        }
        map.get(msg.getId()).put(name, new Pair<>(blocker, consumer));
    }

    public void add(Message msg, String name, Consumer<GenericMessageReactionEvent> consumer) {
        add(msg, name, null, consumer);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MessageDeleteEvent) {
            map.remove(((MessageDeleteEvent) event).getMessageId());
        } else if (event instanceof MessageBulkDeleteEvent) {
            map.keySet().removeAll(((MessageBulkDeleteEvent) event).getMessageIds());
        } else if (event instanceof GenericMessageReactionEvent) {
            GenericMessageReactionEvent e = (GenericMessageReactionEvent) event;
            if (!e.getUser().isBot()) {
                String id = e.getMessageId();
                if (map.containsKey(id)) {
                    if (map.get(id).containsKey(e.getReactionEmote().getName())) {
                        User u = map.get(id).get(e.getReactionEmote().getName()).getKey();
                        if (u == null || u.getIdLong() == ((GenericMessageReactionEvent) event).getUser().getIdLong()) {
                            map.get(id).get(e.getReactionEmote().getName()).getValue().accept(e);
                        }
                    }
                }
            }
        }
    }

    public void removeAll(Message msg) {
        msg.clearReactions().queue();
        map.remove(msg.getId());
    }

}
