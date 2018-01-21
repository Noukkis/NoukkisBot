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

import java.awt.Color;
import java.net.MalformedURLException;
import javax.json.JsonObject;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 *
 * @author Noukkis
 */
public class WolframAlphaHelp {

    private static final String API_KEY = "XWUK7R-ATXA73RPEQ";
    private static final String BASE_URL = "http://api.wolframalpha.com/v2/query?output=json&format=plaintext,image&appid=" + API_KEY + "&input=";

    public static MessageEmbed compute(String query) {
        try {
            JsonObject res = Help.httpJsonGet(BASE_URL, query).getJsonObject("queryresult");
            if (res.getBoolean("success")) {
                EmbedBuilder builder = new EmbedBuilder().setColor(new Color(255, 126, 0));
                builder.setTitle("WolframAlpha's result");
                builder.setDescription("for query : \"" + query + "\"");
                String img = "";
				String imgTitle = "";
                for (JsonObject pod : res.getJsonArray("pods").getValuesAs(JsonObject.class)) {
                    String title = pod.getString("title");
                    JsonObject sub = pod.getJsonArray("subpods").getValuesAs(JsonObject.class).get(0);
                    String value = sub.getString("plaintext");
                    if (!value.isEmpty()) {
                        builder.addField(title, value, false);
                   } else {
						if (title.equalsIgnoreCase("image")) {
							builder.setThumbnail(sub.getJsonObject("img").getString("src"));
						} else {
							imgTitle = title;
							img = sub.getJsonObject("img").getString("src");
						}
                    }
                }
                if(!img.isEmpty()) {
					builder.addField(imgTitle, "", false);
                    builder.setImage(img);
                }
                return builder.build();
            }
        } catch (MalformedURLException ex) {
        }
        return null;
    }

}
