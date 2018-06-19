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
package noukkisBot.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import java.nio.charset.Charset;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;

/**
 *
 * @author Noukkis
 */
public class DiscordAppender extends AppenderBase<ILoggingEvent> {

    private Encoder<ILoggingEvent> encoder;
    private MessageChannel chan;

    @Override
    protected void append(ILoggingEvent e) {
        try {
            if (chan != null) {
                String log = new String(encoder.encode(e), Charset.defaultCharset());
                MessageBuilder builder = new MessageBuilder(log);
                builder.buildAll(MessageBuilder.SplitPolicy.NEWLINE)
                        .forEach((msg) -> {
                            chan.sendMessage(msg).complete();
                        });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public MessageChannel getChan() {
        return chan;
    }

    public void setChan(MessageChannel chan) {
        this.chan = chan;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder) {
        this.encoder = encoder;
    }

    public Encoder<ILoggingEvent> getEncoder() {
        return encoder;
    }
}
