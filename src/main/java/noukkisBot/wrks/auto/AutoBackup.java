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

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.io.Files;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import net.dv8tion.jda.core.JDA;
import noukkisBot.helpers.Help;
import noukkisBot.helpers.Jsonifier;
import noukkisBot.wrks.GuildontonManager;

/**
 *
 * @author Noukkis
 */
public class AutoBackup extends TimerTask {

    private final JDA jda;
    private final File file;
    private final Timer timer;
    private final Jsonifier jsonifier;

    public AutoBackup(JDA jda, File file) {
        this.jda = jda;
        this.file = file;
        this.timer = new Timer("AutoBackup");
        this.jsonifier = new Jsonifier();
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
        String json = jsonifier.toJson(GuildontonManager.getInstance().getMap());
        BufferedWriter out = null;
        try {
            out = Files.newWriter(file, Charset.forName("UTF-8"));
            out.write(json);
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
            try {
                String json = Files.asCharSource(file, Charset.forName("UTF-8")).read();
                return GuildontonManager.unserialize(jda, jsonifier.fromJson(json));
            } catch (Exception ex) {
                Help.LOGGER.error("Unloadable Backup file", ex);
            }
        }
        return false;
    }
}
