/*
 * Decompiled with CFR 0.145.
 */
package com.massivecraft.factions.zcore.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DiscUtil {
    private static final String UTF8 = "UTF-8";

    public static byte[] readBytes(File file) throws IOException {
        int length = (int) file.length();
        byte[] output = new byte[length];
        FileInputStream in = new FileInputStream(file);
        for (int offset = 0; offset < length; offset += in.read(output, offset, (length - offset))) {
        }
        ((InputStream) in).close();
        return output;
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    public static void write(File file, String content) throws IOException {
        DiscUtil.writeBytes(file, DiscUtil.utf8(content));
    }

    public static String read(File file) throws IOException {
        return DiscUtil.utf8(DiscUtil.readBytes(file));
    }

    public static boolean writeCatch(File file, String content) {
        try {
            DiscUtil.write(file, content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String readCatch(File file) {
        try {
            return DiscUtil.read(file);
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] utf8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

