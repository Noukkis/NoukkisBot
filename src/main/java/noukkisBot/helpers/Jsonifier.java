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

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import noukkisBot.wrks.GuildontonManager;
import noukkisBot.wrks.GuildontonManager.Guildonton;

/**
 *
 * @author Noukkis
 */
public class Jsonifier {

    private Gson gson;
    private JsonParser parser;

    public Jsonifier() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        parser = new JsonParser();
    }

    public String toJson(HashMap<Long, ClassToInstanceMap<GuildontonManager.Guildonton>> src) {
        JsonObject res = new JsonObject();
        src.forEach((guildID, ctim) -> {
            JsonObject guild = new JsonObject();
            res.add(guildID + "", guild);
            ctim.forEach((cl, instance) -> {
                guild.add(cl.getCanonicalName(), gson.toJsonTree(instance));
            });
        });
        return gson.toJson(res);
    }

    public HashMap<Long, ClassToInstanceMap<GuildontonManager.Guildonton>> fromJson(String string) throws JsonParseException {
        JsonObject json = parser.parse(string).getAsJsonObject();
        HashMap<Long, ClassToInstanceMap<GuildontonManager.Guildonton>> res = new HashMap<>();
        json.entrySet().forEach((guildEntry) -> {
            String guildID = guildEntry.getKey();
            JsonObject jCtim = guildEntry.getValue().getAsJsonObject();
            ClassToInstanceMap<GuildontonManager.Guildonton> ctim = MutableClassToInstanceMap.create();
            res.put(Long.parseLong(guildID), ctim);
            jCtim.entrySet().forEach((tonEntry) -> {
                try {
                    Class cl = Class.forName(tonEntry.getKey());
                    Guildonton guildonton = (Guildonton) gson.fromJson(tonEntry.getValue(), cl);
                    ctim.put(cl, guildonton);
                } catch (ClassNotFoundException ex) {
                    throw new JsonParseException(ex);
                }
            });
        });
        return res;
    }

}
