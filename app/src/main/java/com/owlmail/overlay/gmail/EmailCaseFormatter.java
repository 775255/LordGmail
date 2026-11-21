package com.owlmail.overlay.gmail;

import java.util.Locale;
/** Display/copy case only: Gmail does not distinguish a new account by letter case. */
public final class EmailCaseFormatter {
    private EmailCaseFormatter() { }
    public static String format(String address, int variant) {
        if (address == null) return "";
        int at = address.indexOf('@');
        if (at < 1) return address;
        String local = address.substring(0,at).toLowerCase(Locale.ROOT);
        String domain = address.substring(at).toLowerCase(Locale.ROOT);
        int mode = Math.floorMod(variant, 4);
        if (mode == 0) return local + domain;
        StringBuilder b = new StringBuilder(local.length() + domain.length());
        for (int i=0;i<local.length();i++) {
            char ch=local.charAt(i);
            boolean upper=mode==1 || (mode==2 && i%2==0) || (mode==3 && i%2!=0);
            b.append(upper ? Character.toUpperCase(ch) : ch);
        }
        return b.append(domain).toString();
    }
}
