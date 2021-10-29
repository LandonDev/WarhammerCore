package com.cosmicpvp.cosmicutils.utils;

import org.bukkit.ChatColor;

public class CC {
    public static final String Black = ChatColor.BLACK.toString();

    public static final String BlackB = ChatColor.BLACK + ChatColor.BOLD.toString();

    public static final String BlackI = ChatColor.BLACK + ChatColor.ITALIC.toString();

    public static final String BlackU = ChatColor.BLACK + ChatColor.UNDERLINE.toString();

    public static final String DarkBlue = ChatColor.DARK_BLUE.toString();

    public static final String DarkBlueB = ChatColor.DARK_BLUE + ChatColor.BOLD.toString();

    public static final String DarkBlueI = ChatColor.DARK_BLUE + ChatColor.ITALIC.toString();

    public static final String DarkBlueU = ChatColor.DARK_BLUE + ChatColor.UNDERLINE.toString();

    public static final String DarkGreen = ChatColor.DARK_GREEN.toString();

    public static final String DarkGreenB = ChatColor.DARK_GREEN + ChatColor.BOLD.toString();

    public static final String DarkGreenI = ChatColor.DARK_GREEN + ChatColor.ITALIC.toString();

    public static final String DarkGreenU = ChatColor.DARK_GREEN + ChatColor.UNDERLINE.toString();

    public static final String DarkAqua = ChatColor.DARK_AQUA.toString();

    public static final String DarkAquaB = ChatColor.DARK_AQUA + ChatColor.BOLD.toString();

    public static final String DarkAquaI = ChatColor.DARK_AQUA + ChatColor.ITALIC.toString();

    public static final String DarkAquaU = ChatColor.DARK_AQUA + ChatColor.UNDERLINE.toString();

    public static final String DarkRed = ChatColor.DARK_RED.toString();

    public static final String DarkRedB = ChatColor.DARK_RED + ChatColor.BOLD.toString();

    public static final String DarkRedI = ChatColor.DARK_RED + ChatColor.ITALIC.toString();

    public static final String DarkRedU = ChatColor.DARK_RED + ChatColor.UNDERLINE.toString();

    public static final String DarkPurple = ChatColor.DARK_PURPLE.toString();

    public static final String DarkPurpleB = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString();

    public static final String DarkPurpleI = ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString();

    public static final String DarkPurpleU = ChatColor.DARK_PURPLE + ChatColor.UNDERLINE.toString();

    public static final String Gold = ChatColor.GOLD.toString();

    public static final String GoldB = ChatColor.GOLD + ChatColor.BOLD.toString();

    public static final String GoldI = ChatColor.GOLD + ChatColor.ITALIC.toString();

    public static final String GoldU = ChatColor.GOLD + ChatColor.UNDERLINE.toString();

    public static final String Gray = ChatColor.GRAY.toString();

    public static final String GrayB = ChatColor.GRAY + ChatColor.BOLD.toString();

    public static final String GrayI = ChatColor.GRAY + ChatColor.ITALIC.toString();

    public static final String GrayU = ChatColor.GRAY + ChatColor.UNDERLINE.toString();

    public static final String DarkGray = ChatColor.DARK_GRAY.toString();

    public static final String DarkGrayB = ChatColor.DARK_GRAY + ChatColor.BOLD.toString();

    public static final String DarkGrayI = ChatColor.DARK_GRAY + ChatColor.ITALIC.toString();

    public static final String DarkGrayU = ChatColor.DARK_GRAY + ChatColor.UNDERLINE.toString();

    public static final String Blue = ChatColor.BLUE.toString();

    public static final String BlueB = ChatColor.BLUE + ChatColor.BOLD.toString();

    public static final String BlueI = ChatColor.BLUE + ChatColor.ITALIC.toString();

    public static final String BlueU = ChatColor.BLUE + ChatColor.UNDERLINE.toString();

    public static final String Green = ChatColor.GREEN.toString();

    public static final String GreenB = ChatColor.GREEN + ChatColor.BOLD.toString();

    public static final String GreenI = ChatColor.GREEN + ChatColor.ITALIC.toString();

    public static final String GreenU = ChatColor.GREEN + ChatColor.UNDERLINE.toString();

    public static final String Aqua = ChatColor.AQUA.toString();

    public static final String AquaB = ChatColor.AQUA + ChatColor.BOLD.toString();

    public static final String AquaI = ChatColor.AQUA + ChatColor.ITALIC.toString();

    public static final String AquaU = ChatColor.AQUA + ChatColor.UNDERLINE.toString();

    public static final String Red = ChatColor.RED.toString();

    public static final String RedB = ChatColor.RED + ChatColor.BOLD.toString();

    public static final String RedI = ChatColor.RED + ChatColor.ITALIC.toString();

    public static final String RedU = ChatColor.RED + ChatColor.UNDERLINE.toString();

    public static final String LightPurple = ChatColor.LIGHT_PURPLE.toString();

    public static final String LightPurpleB = ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString();

    public static final String LightPurpleI = ChatColor.LIGHT_PURPLE + ChatColor.ITALIC.toString();

    public static final String LightPurpleU = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString();

    public static final String Yellow = ChatColor.YELLOW.toString();

    public static final String YellowB = ChatColor.YELLOW + ChatColor.BOLD.toString();

    public static final String YellowI = ChatColor.YELLOW + ChatColor.ITALIC.toString();

    public static final String YellowU = ChatColor.YELLOW + ChatColor.UNDERLINE.toString();

    public static final String White = ChatColor.WHITE.toString();

    public static final String WhiteB = ChatColor.WHITE + ChatColor.BOLD.toString();

    public static final String WhiteI = ChatColor.WHITE + ChatColor.ITALIC.toString();

    public static final String WhiteU = ChatColor.WHITE + ChatColor.UNDERLINE.toString();

    public static final String Bold = ChatColor.BOLD.toString();

    public static final String Strike = ChatColor.STRIKETHROUGH.toString();

    public static final String Underline = ChatColor.UNDERLINE.toString();

    public static final String Magic = ChatColor.MAGIC.toString();

    public static final String Italic = ChatColor.ITALIC.toString();

    public static final String Reset = ChatColor.RESET.toString();

    public static final String Go = GreenB + "<!> " + Green;

    public static final String Wait = YellowB + "<!> " + Yellow;

    public static final String Stop = RedB + "<!> " + Red;

    public static String prefix(char color) {
        return translate("&" + color + "&l<!> &" + color);
    }

    public static String translate(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String strip(String string) {
        return ChatColor.stripColor(string);
    }
}
