package com.owlmail.overlay.gmail;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.SecureRandom;
import java.util.Locale;

/** Generates and retains Gmail plus-address aliases per original inbox. */
public final class EmailAliasManager {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PREFS = "lord_gmail_aliases";
    private EmailAliasManager() { }

    private static String key(String address) {
        return "alias_" + address.toLowerCase(Locale.ROOT);
    }
    public static String display(Context context, String address) {
        if (address == null || address.trim().isEmpty()) return "";
        String alias = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(key(address), "");
        return alias.isEmpty() ? address : alias;
    }
    public static String next(Context context, String address) {
        if (address == null) return "";
        int at = address.indexOf('@');
        if (at <= 0) return address;
        String local = address.substring(0, at);
        // Existing plus tags belong to the same inbox; append a new tag to its base address.
        int plus = local.indexOf('+');
        if (plus >= 0) local = local.substring(0, plus);
        String domain = address.substring(at).toLowerCase(Locale.ROOT);
        String previous = display(context, address);
        String candidate;
        do {
            StringBuilder suffix = new StringBuilder(6);
            for (int i = 0; i < 6; i++) suffix.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
            candidate = local + "+" + suffix + domain;
        } while (candidate.equals(previous));
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        // Store every generated alias so previous addresses remain available for matching.
        String historyKey = "history_" + address.toLowerCase(Locale.ROOT);
        String history = prefs.getString(historyKey, "");
        prefs.edit().putString(key(address), candidate)
                .putString(historyKey, history + candidate + "\n").apply();
        return candidate;
    }
}
