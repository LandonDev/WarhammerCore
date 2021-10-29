/*
 * Decompiled with CFR 0.145.
 *
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 */
package com.massivecraft.factions.zcore.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    public static final transient Pattern patternTag = Pattern.compile("<([a-zA-Z0-9_]*)>");
    private static final String titleizeLine = TextUtil.repeat("_", 52);
    private static final int titleizeBalance = -1;
    public Map<String, String> tags = new HashMap<String, String>();

    public static String replaceTags(String str, Map<String, String> tags) {
        StringBuffer ret = new StringBuffer();
        Matcher matcher = patternTag.matcher(str);
        while (matcher.find()) {
            String tag = matcher.group(1);
            String repl = tags.get(tag);
            if (repl == null) {
                matcher.appendReplacement(ret, "<" + tag + ">");
                continue;
            }
            matcher.appendReplacement(ret, repl);
        }
        matcher.appendTail(ret);
        return ret.toString();
    }

    public static String parseColor(String string) {
        string = TextUtil.parseColorAmp(string);
        string = TextUtil.parseColorAcc(string);
        string = TextUtil.parseColorTags(string);
        return string;
    }

    public static String parseColorAmp(String string) {
        string = string.replaceAll("(\u00a7([a-z0-9]))", "\u00a7$2");
        string = string.replaceAll("(&([a-z0-9]))", "\u00a7$2");
        string = string.replace("&&", "&");
        return string;
    }

    public static String parseColorAcc(String string) {
        return string.replace("`e", "").replace("`r", ChatColor.RED.toString()).replace("`R", ChatColor.DARK_RED.toString()).replace("`y", ChatColor.YELLOW.toString()).replace("`Y", ChatColor.GOLD.toString()).replace("`g", ChatColor.GREEN.toString()).replace("`G", ChatColor.DARK_GREEN.toString()).replace("`a", ChatColor.AQUA.toString()).replace("`A", ChatColor.DARK_AQUA.toString()).replace("`b", ChatColor.BLUE.toString()).replace("`B", ChatColor.DARK_BLUE.toString()).replace("`p", ChatColor.LIGHT_PURPLE.toString()).replace("`P", ChatColor.DARK_PURPLE.toString()).replace("`k", ChatColor.BLACK.toString()).replace("`s", ChatColor.GRAY.toString()).replace("`S", ChatColor.DARK_GRAY.toString()).replace("`w", ChatColor.WHITE.toString());
    }

    public static String parseColorTags(String string) {
        return string.replace("<empty>", "").replace("<black>", "\u00a70").replace("<navy>", "\u00a71").replace("<green>", "\u00a72").replace("<teal>", "\u00a73").replace("<red>", "\u00a74").replace("<purple>", "\u00a75").replace("<gold>", "\u00a76").replace("<silver>", "\u00a77").replace("<gray>", "\u00a78").replace("<blue>", "\u00a79").replace("<lime>", "\u00a7a").replace("<aqua>", "\u00a7b").replace("<rose>", "\u00a7c").replace("<pink>", "\u00a7d").replace("<yellow>", "\u00a7e").replace("<white>", "\u00a7f");
    }

    public static String upperCaseFirst(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String implode(List<String> list, String glue) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < list.size(); ++i) {
            if (i != 0) {
                ret.append(glue);
            }
            ret.append(list.get(i));
        }
        return ret.toString();
    }

    public static String repeat(String s, int times) {
        if (times <= 0) {
            return "";
        }
        return s + TextUtil.repeat(s, times - 1);
    }

    public static String getMaterialName(Material material) {
        return material.toString().replace('_', ' ').toLowerCase();
    }

    public static String getMaterialName(int materialId) {
        return TextUtil.getMaterialName(Material.getMaterial(materialId));
    }

    public static String getBestStartWithCI(Collection<String> candidates, String start) {
        String ret = null;
        int best = 0;
        start = start.toLowerCase();
        int minlength = start.length();
        for (String candidate : candidates) {
            if (candidate.length() < minlength || !candidate.toLowerCase().startsWith(start)) continue;
            int lendiff = candidate.length() - minlength;
            if (lendiff == 0) {
                return candidate;
            }
            if (lendiff >= best && best != 0) continue;
            best = lendiff;
            ret = candidate;
        }
        return ret;
    }

    public String parse(String str, Object... args) {
        String msg = String.format(this.parse(str), args);
        String pre = "";
        if (msg.startsWith(ChatColor.RED.toString()) && !msg.contains("(!)")) {
            pre = ChatColor.RED + "" + ChatColor.BOLD + "(!) ";
        }
        if (msg.startsWith(ChatColor.YELLOW.toString()) && !msg.contains("(!)")) {
            pre = ChatColor.YELLOW + "" + ChatColor.BOLD + "(!) ";
        }
        return pre + msg;
    }

    public String parse(String str) {
        return this.parseTags(TextUtil.parseColor(str));
    }

    public String parseTags(String str) {
        return TextUtil.replaceTags(str, this.tags);
    }

    public String titleize(String str) {
        String center = ChatColor.translateAlternateColorCodes('&',"&8&m-------------- &8<&4") + this.parseTags(ChatColor.translateAlternateColorCodes('&',"<l>&4")) + str + this.parseTags(ChatColor.translateAlternateColorCodes('&',"<a>&4")) + ChatColor.translateAlternateColorCodes('&',"&8> &8&m--------------");
        int centerlen = ChatColor.stripColor(center).length();
        int pivot = titleizeLine.length() / 2;
        int eatLeft = centerlen / 2 - -1;
        int eatRight = centerlen - eatLeft + -1;
        if (eatLeft < pivot) {
            return this.parseTags("<a>")  + center;
        }
        return this.parseTags("<a>") + center;
    }

    public ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title) {
        ArrayList<String> ret = new ArrayList<String>();
        int pageZeroBased = pageHumanBased - 1;
        int pageheight = 9;
        int pagecount = lines.size() / pageheight + 1;
        ret.add(this.titleize(title + " " + pageHumanBased + "/" + pagecount));
        if (pagecount == 0) {
            ret.add(this.parseTags("<i>Sorry. No Pages available."));
            return ret;
        }
        if (pageZeroBased < 0 || pageHumanBased > pagecount) {
            ret.add(this.parseTags("<i>Invalid page. Must be between 1 and " + pagecount));
            return ret;
        }
        int from = pageZeroBased * pageheight;
        int to = from + pageheight;
        if (to > lines.size()) {
            to = lines.size();
        }
        ret.addAll(lines.subList(from, to));
        return ret;
    }
}

