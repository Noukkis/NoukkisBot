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
package noukkisBot.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import noukkisBot.wrks.ReactButtonsMaker;

/**
 *
 * @author Noukkis
 */
public class SearchResult<E> {

    private static final int MAX = 5;

    private final List<E> list;
    private final List<E> currents;
    private final TextChannel chan;
    private final User author;
    private final int maxPage;
    private final Consumer<E> selector;
    
    private int page;
    private String title;
    private Function<E, String> lineMaker;

    public SearchResult(TextChannel chan, Member member, List<E> list, Consumer<E> selector) {
        this.list = list;
        this.currents = new ArrayList<>();
        this.chan = chan;
        this.author = member.getUser();
        this.maxPage = (list.size() / MAX) + 1;
        this.page = 0;
        this.selector = selector;
        title = "Result";
        lineMaker = (E e) -> e.toString();
    }

    public void start() {
        ReactButtonsMaker rbm = ReactButtonsMaker.getInstance();
        chan.sendMessage(createMsg()).queue((msg) -> {
            rbm.add(msg, "❌", author, (event) -> stop(msg));
            if (list.size() > 5) {
                rbm.add(msg, "◀", author, (event) -> previous(msg));
            }
            int max = list.size() > MAX ? MAX : list.size();
            for (int i = 0; i < max; i++) {
                final int j = i;
                rbm.add(msg, Help.NUMBERS_REACTS[i + 1], author, (event) -> select(j, msg));
            }
            if (list.size() > 5) {
                rbm.add(msg, "▶", author, (event) -> next(msg));
            }
        });
    }

    private String createMsg() {
        currents.clear();
        int min = page * MAX;
        String msg = title + "\n```markdown\n";

        for (int i = min; i < min + MAX && i < list.size(); i++) {
            currents.add(list.get(i));
            String title = lineMaker.apply(list.get(i));
            msg += "\n" + (i - min + 1) + ". " + title;
        }
        return msg + "```\nPage " + (page + 1) + "/" + maxPage;
    }

    private void previous(Message msg) {
        page--;
        if (page < 0) {
            page = maxPage - 1;
        }
        msg.editMessage(createMsg()).queue();
    }

    private void next(Message msg) {
        page++;
        if (page >= maxPage) {
            page = 0;
        }
        msg.editMessage(createMsg()).queue();
    }

    private void select(int i, Message msg) {
        int index = page * MAX + i;
        if (index < list.size()) {
            selector.accept(list.get(index));
            stop(msg);
        }
    }

    private void stop(Message msg) {
        msg.delete().queue();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLineMaker(Function<E, String> lineMaker) {
        this.lineMaker = lineMaker;
    }
}
