package noukkisBot.helpers;

/**
 *
 * @author Noukkis
 */
public abstract class ExtendedThread extends Thread {

    private final static long MAX_PAUSE = 1000;

    private boolean running;
    private long waitingTime;
    private boolean setupDone;
    private boolean started;
    private long paused;

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
