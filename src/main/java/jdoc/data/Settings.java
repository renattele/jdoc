package jdoc.data;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class Settings {
    private static final String RECENT_KEY = "recent";
    private static final Preferences preferences = Preferences.userNodeForPackage(Settings.class);
    public static Preferences get() {
        return preferences;
    }

    public static void addRecent(String url) {
        var recent = getRecent();
        recent.add(url);
        get().put(RECENT_KEY, Serializer.gson().toJson(recent));
    }

    @SuppressWarnings("unchecked")
    public static List<String> getRecent() {
        var recentStr = get().get(RECENT_KEY, "");
        var recent = (List<String>) Serializer.gson().fromJson(recentStr, List.class);
        return recent != null ? recent : new ArrayList<>();
    }
}
