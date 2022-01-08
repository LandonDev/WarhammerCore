package landon.jurassiccore.utils;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;





public class StringUtil
{
    public static String sanitizeFileName(String name) { return INVALIDFILECHARS.matcher(name.toLowerCase(Locale.ENGLISH)).replaceAll("_"); }



    public static String safeString(String string) { return STRICTINVALIDCHARS.matcher(string.toLowerCase(Locale.ENGLISH)).replaceAll("_"); }



    public static String sanitizeString(String string) { return INVALIDCHARS.matcher(string).replaceAll(""); }



    public static String joinList(Object... list) { return joinList(", ", list); }


    public static String joinList(String seperator, Object... list) {
        StringBuilder buf = new StringBuilder();
        for (Object each : list) {
            if (buf.length() > 0) {
                buf.append(seperator);
            }
            if (each instanceof Collection) {
                buf.append(joinList(seperator, ((Collection)each).toArray()));
            } else {

                try {
                    buf.append(each.toString());
                }
                catch (Exception e) {
                    buf.append(each.toString());
                }
            }
        }
        return buf.toString();
    }

    public static String joinListSkip(String seperator, String skip, Object... list) {
        StringBuilder buf = new StringBuilder();
        for (Object each : list) {
            if (!each.toString().equalsIgnoreCase(skip)) {
                if (buf.length() > 0) {
                    buf.append(seperator);
                }
                if (each instanceof Collection) {
                    buf.append(joinListSkip(seperator, skip, ((Collection)each).toArray()));
                } else {

                    try {
                        buf.append(each.toString());
                    }
                    catch (Exception e) {
                        buf.append(each.toString());
                    }
                }
            }
        }
        return buf.toString();
    }





    private static final Pattern INVALIDFILECHARS = Pattern.compile("[^a-z0-9-]");
    private static final Pattern STRICTINVALIDCHARS = Pattern.compile("[^a-z0-9]");
    private static final Pattern INVALIDCHARS = Pattern.compile("[^\t\n\r -~?�-??-?]");
}

