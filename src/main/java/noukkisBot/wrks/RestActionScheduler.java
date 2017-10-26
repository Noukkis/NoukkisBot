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

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import net.dv8tion.jda.core.requests.RestAction;

/**
 *
 * @author Noukkis
 */
public class RestActionScheduler<E> {

    private final int delay;
    private final Timer timer;
    private RestAction<E> lastRa;
    private Consumer<E> lastSuccess;
    private Consumer<Throwable> lastFailure;
    private RasTask task;

    public RestActionScheduler(int delay) {
        this.delay = delay;
        this.timer = new Timer();
    }

    public void schedule(RestAction<E> ra, boolean now) {
        schedule(ra, now, null, null);
    }

    public void schedule(RestAction<E> ra, boolean now, Consumer<E> success, Consumer<Throwable> failure) {
        if (task != null) {
            task.cancel();
        }
        lastRa = ra;
        lastSuccess = success;
        lastFailure = failure;
        task = new RasTask();
        if (now) {
            task.run();
        } else {
            timer.schedule(task, delay);
        }
    }

    private void nullify() {
        lastSuccess = null;
        lastFailure = null;
        lastRa = null;
        task = null;
    }

    public class RasTask extends TimerTask {

        @Override
        public void run() {
            if (lastRa != null) {
                if (lastFailure != null) {
                    lastRa.queue(lastSuccess, lastFailure);
                } else if (lastSuccess != null) {
                    lastRa.queue(lastSuccess);
                } else {
                    lastRa.queue();
                }
            }
            nullify();
        }

        @Override
        public boolean cancel() {
            nullify();
            return super.cancel();
        }

    }

}
