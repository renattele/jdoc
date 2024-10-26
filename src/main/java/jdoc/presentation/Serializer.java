package jdoc.presentation;

import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

public class Serializer {
    private static Gson gson;

    public static Gson gson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(AbstractDelta.class, (JsonDeserializer<AbstractDelta<String>>) (jsonElement, type, context) -> {
                        var diffType = jsonElement.getAsJsonObject().get("type").getAsString();
                        switch (diffType) {
                            case "CHANGE" -> {
                                return context.deserialize(jsonElement, ChangeDelta.class);
                            }
                            case "INSERT" -> {
                                return context.deserialize(jsonElement, InsertDelta.class);
                            }
                            case "DELETE" -> {
                                return context.deserialize(jsonElement, DeleteDelta.class);
                            }
                        }
                        return null;
                    })
                    .create();
        }
        return gson;
    }
}
