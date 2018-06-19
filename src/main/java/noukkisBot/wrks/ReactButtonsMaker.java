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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Consumer;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
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

    private final HashMap<String, HashMap<String, SimpleEntry<Blocker, Consumer<GenericMessageReactionEvent>>>> map;

    public static ReactButtonsMaker getInstance() {
        return INSTANCE;
    }

    private ReactButtonsMaker() {
        map = new HashMap<>();
    }

    public void add(Message msg, Emote emote, Blocker blocker, Consumer<GenericMessageReactionEvent> consumer) {
        msg.addReaction(emote).queue();
        if (!map.containsKey(msg.getId())) {
            map.put(msg.getId(), new HashMap<>());
        }
        map.get(msg.getId()).put(emote.getName(), new SimpleEntry<>(blocker, consumer));
    }

    public void add(Message msg, Emote emote, Consumer<GenericMessageReactionEvent> consumer) {
        add(msg, emote, null, consumer);
    }

    public void add(Message msg, String name, Blocker blocker, Consumer<GenericMessageReactionEvent> consumer) {
        msg.addReaction(name).queue();
        if (!map.containsKey(msg.getId())) {
            map.put(msg.getId(), new HashMap<>());
        }
        map.get(msg.getId()).put(name, new SimpleEntry<>(blocker, consumer));
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
                        Blocker blocker = map.get(id).get(e.getReactionEmote().getName()).getKey();
                        if (blocker == null || blocker.authorised(e.getMember())) {
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

    public static class Blocker {

        private final ArrayList<Member> members;
        private final ArrayList<Role> roles;
        private final ArrayList<Permission> perms;

        public static Blocker from(Member... members) {
            Blocker blocker = new Blocker();
            blocker.members.addAll(Arrays.asList(members));
            return blocker;
        }

        public static Blocker from(Role... roles) {
            Blocker blocker = new Blocker();
            blocker.roles.addAll(Arrays.asList(roles));
            return blocker;
        }

        public static Blocker from(Permission... perms) {
            Blocker blocker = new Blocker();
            blocker.perms.addAll(Arrays.asList(perms));
            return blocker;
        }

        public Blocker() {
            members = new ArrayList<>();
            roles = new ArrayList<>();
            perms = new ArrayList<>();
        }

        public ArrayList<Member> getMembers() {
            return members;
        }

        public ArrayList<Role> getRoles() {
            return roles;
        }

        public ArrayList<Permission> getPerms() {
            return perms;
        }

        public boolean authorised(Member m) {
            return members.contains(m)
                    || (!roles.isEmpty() && !Collections.disjoint(m.getRoles(), roles))
                    || (!perms.isEmpty() && m.hasPermission(perms));
        }
    }
}
