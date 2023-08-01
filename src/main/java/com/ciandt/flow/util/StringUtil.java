package com.ciandt.flow.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Wuzi
 */
public class StringUtil extends com.intellij.openapi.util.text.StringUtil {

    public static boolean isNumber(String s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static @NotNull String stripHtml(@NotNull String html, @Nullable String breaks) {
        if (breaks != null) {
            html = html.replaceAll("<br/?>", breaks);
        }

        return html.replaceAll("<(.|\n)*?>", "");
    }

    public static String timestampNow() {
        OffsetDateTime dateTime = OffsetDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return dateTime.format(dateTimeFormatter);
    }
}
