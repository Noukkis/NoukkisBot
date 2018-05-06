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
package noukkisBot.helpers;

import java.io.Serializable;

/**
 *
 * @author Noukkis
 */
public abstract class ExtendedThread extends Thread implements Serializable {

    private final static long MAX_PAUSE = 1000;

    private long waitingTime;
    
    private transient boolean running;
    private transient boolean setupDone;
    private transient boolean started;
    private transient long paused;

    public ExtendedThread(long waitingTime) {
        this.waitingTime = waitingTime > 0 ? waitingTime : 0;
        this.running = false;
        this.setupDone = false;
        this.started = false;
        this.paused = 0;
    }

    public ExtendedThread() {
        this(0);
    }

    @Override
    public synchronized void start() {
        super.start();
        started = true;
    }

    public final boolean setupAndStart() {
        setupDone = setup();
        if (setupDone) {
            start();
        }
        return setupDone;
    }

    @Override
    public final void run() {
        if (setupDone || setup()) {
            running = true;
            while (running) {
                if (paused == 0) {
                    execute();
                }
                hold(waitingTime);
                executePause();
            }
            teardown();
        }
    }

    public final void pause() {
        paused = -1;
    }

    public final void pause(long milli) {
        paused = milli;
    }

    public final void unpause() {
        paused = 0;
    }

    private final void hold(long milli) {
        if (milli > MAX_PAUSE) {
            hold(MAX_PAUSE);
            if (running) {
                hold(milli - MAX_PAUSE);
            }
        } else {
            try {
                sleep(milli);
            } catch (InterruptedException ex) {
            }
        }
    }

    private final void executePause() {
        if (paused > waitingTime) {
            hold(paused - waitingTime);
            paused = 0;
        }
    }

    protected boolean setup() {
        return true;
    }

    protected void teardown() {
    }

    protected abstract void execute();

    public boolean isRunning() {
        return running;
    }

    public void close() {
        this.running = false;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(long waitingTime) {
        this.waitingTime = waitingTime;
    }

    public boolean isSetupDone() {
        return setupDone;
    }

    public boolean isStarted() {
        return started;
    }
}
