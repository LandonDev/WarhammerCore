package com.massivecraft.factions.util;

import java.io.*;
import java.lang.reflect.Type;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

public class JSONUtils {
    public static Gson gson = (new GsonBuilder()).enableComplexMapKeySerialization().create();

    public static File getOrCreateFile(File parent, String string) throws IOException {
        try {
            if (!parent.exists()) {
                parent.mkdir();
                Bukkit.getLogger().info("Creating directory " + parent.getName());
            }
            File f = new File(parent, string);
            if (!f.exists()) {
                Bukkit.getLogger().info("Creating new file " + string + " due to it not existing!");
                f.createNewFile();
            }
            return f;
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static File getOrCreateFile(String fileName) throws IOException {
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                Bukkit.getLogger().info("Creating new file " + fileName + " due to it not existing!");
                f.createNewFile();
            }
            return f;
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static Object fromJson(String fileName, Object token) throws IOException {
        try {
            File f = getOrCreateFile(fileName);
            return fromJson(f, token);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static Object fromJson(File f, Object clazz) throws FileNotFoundException {
        try {
            return gson.fromJson(new FileReader(f), getTypeFromObject(clazz));
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static Object fromJson(File f, Object clazz, Object defaultObj) throws FileNotFoundException {
        try {
            Object retr = gson.fromJson(new FileReader(f), getTypeFromObject(clazz));
            if (retr == null)
                return defaultObj;
            return retr;
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static Object fromJson(File f, Type token) throws FileNotFoundException {
        try {
            return fromJson(f, token, gson);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static Object fromJson(File f, Type token, Gson gson) throws FileNotFoundException {
        try {
            return gson.fromJson(new FileReader(f), token);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static String toJSON(Object object, Object token) {
        try {
            return toJSON(object, token, gson);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static String toJSON(Object object, Object token, Gson gson) {
        try {
            return gson.toJson(object, getTypeFromObject(token));
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static boolean saveJSONToFile(String fileName, Object toSave, Object token) throws IOException {
        try {
            return saveJSONToFile(getOrCreateFile(fileName), toSave, token);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static boolean saveJSONToFile(File f, Object toSave, Object token, Gson gson) throws IOException {
        try {
            String str = toJSON(toSave, token, gson);
            FileWriter writer = new FileWriter(f);
            writer.write(str);
            writer.flush();
            writer.close();
            return true;
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static boolean saveJSONToFile(File f, Object toSave, Object token) throws IOException {
        try {
            return saveJSONToFile(f, toSave, token, gson);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    private static Type getTypeFromObject(Object object) {
        return (object instanceof Type) ? (Type)object : getTypeFromClass(object.getClass());
    }

    private static Type getTypeFromClass(Class<?> clazz) {
        return TypeToken.of(clazz).getType();
    }
}
