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
package noukkisBot.wrks.auto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.core.JDA;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.rss.RssWrk;

/**
 *
 * @author Noukkis
 */
public class AutoBackup extends TimerTask {

    private final JDA jda;
    private final File file;
    private final Timer timer;

    public AutoBackup(JDA jda, File file) {
        this.jda = jda;
        this.file = file;
        this.timer = new Timer("AutoBackup");
    }

    public void scheduleBackup(long time) {
        timer.scheduleAtFixedRate(this, time, time);
    }

    public void stop() {
        timer.cancel();
    }

    @Override
    public void run() {
        backupNow();
    }

    public void backupNow() {
        HashMap<Long, HashMap<String, ArrayList<Long>>> feeds = RssWrk.serialize();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            out.writeObject(feeds);
            out.flush();
            Help.LOGGER.info("Backup successful");
        } catch (IOException ex) {
            Help.LOGGER.error("Can't Backup", ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public boolean recover() {
        if (file.exists() && file.isFile()) {
            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
                HashMap<Long, HashMap<String, ArrayList<Long>>> feeds = (HashMap) in.readObject();
                RssWrk.unserialize(jda, feeds);
                return true;
            } catch (Exception ex) {
                Help.LOGGER.error("Unloadable Backup file", ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
        return false;
    }
}
