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
package noukkisBot.autoresponses;

import java.util.ArrayList;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 *
 * @author Noukkis
 */
public class AutoResponseManager implements EventListener {
    
    private final ArrayList<AutoResponse> responses;

    public AutoResponseManager() {
        this.responses = new ArrayList<>();
    }
    
    public void init() {
        responses.add(new AutoResponse("tupu", "no u"));
		responses.add(new AutoResponse("duku", "no u"));
        responses.add(new AutoResponse("(╯°□°）╯︵ ┻━┻", "┬─┬ ノ( ゜-゜ノ)"));
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof MessageReceivedEvent) {
            String msg = ((MessageReceivedEvent) event).getMessage().getContentRaw();
            MessageChannel chan = ((MessageReceivedEvent) event).getChannel();
            for (AutoResponse autoResponse : responses) {
                if(msg.toLowerCase().contains(autoResponse.trigger)) {
                    chan.sendMessage(autoResponse.response).queue();
                    break;
                }
            }
        }
    }
    
    private static class AutoResponse {
        
        private final String trigger;
        private final String response;

        public AutoResponse(String trigger, String response) {
            this.trigger = trigger;
            this.response = response;
        }
        
    }
}
