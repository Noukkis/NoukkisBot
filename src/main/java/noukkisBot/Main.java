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
package noukkisBot;

import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import java.io.IOException;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import noukkisBot.autoresponses.AutoResponseManager;
import noukkisBot.helpers.Help;
import noukkisBot.wrks.GuildontonManager;
import noukkisBot.wrks.ReactButtonsMaker;
import noukkisBot.wrks.auto.AutoBackup;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        launch();
    }

    private final static int BACKUP_TIME = 1000 * 60 * 60;

    public static void launch() {
        try {
            Help.init();
            AutoResponseManager arm = new AutoResponseManager();
            arm.init();
            CommandClientBuilder ccb = new CommandClientBuilder()
                    .setOwnerId(Help.OWNER_ID)
                    .setEmojis("\u2705", "\u26A0", "\u274C")
                    .setPrefix("-");
            Help.setCommands(ccb);
            Help.setHelp(ccb);
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(Help.BOT_TOKEN)
                    .setGame(Game.playing("loading..."))
                    .addEventListener(ReactButtonsMaker.getInstance())
                    .addEventListener(ccb.build())
                    .addEventListener(GuildontonManager.getInstance())
                    .addEventListener(arm)
                    .buildBlocking();
            Help.BACKUP = new AutoBackup(jda, Help.BACKUP_FILE);
            Help.LOGGER.info(Help.BACKUP.recover() ? "Backup loaded" : "Can't load Backup");
            Help.BACKUP.scheduleBackup(BACKUP_TIME);
            Help.LOGGER.info("Backups scheduled every " + (BACKUP_TIME / 1000) + " seconds");
        } catch (IOException | LoginException | InterruptedException ex) {
            Help.LOGGER.error("Cannot launch the bot", ex);
        }
    }

}
