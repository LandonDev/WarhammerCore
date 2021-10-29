package com.massivecraft.factions.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Bukkit;

public class ReflectionUtils {
    private static Map<String, Field> cachedFields = new HashMap<>();

    private static Map<String, Method> cachedMethods = new HashMap<>();

    private static String nmsVersion;

    public static Class<?> getEntityLivingClass() {
        return entityLivingClass;
    }

    public static Class<?> getGenericAttributesClass() {
        return genericAttributesClass;
    }

    public static Class<?> getIattributeInstanceClass() {
        return iattributeInstanceClass;
    }

    public static Class<?> getAttributeInstanceClass() {
        return attributeInstanceClass;
    }

    static {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = name.substring(name.lastIndexOf('.') + 1) + ".";
        Bukkit.getLogger().info("[CosmicUtils] Registered NMS version: " + nmsVersion);
    }

    private static Class<?> entityLivingClass = getNMSClass("EntityLiving");

    private static Class<?> genericAttributesClass;

    private static Class<?> iattributeInstanceClass;

    private static Class<?> attributeInstanceClass = getNMSClass("AttributeInstance");

    static {
        genericAttributesClass = getNMSClass("GenericAttributes");
        iattributeInstanceClass = getNMSClass("IAttribute");
    }

    public static Field getField(Class<?> clazz, String field) {
        try {
            Field cached = cachedFields.get(clazz.getName() + ":" + field);
            if (cached != null)
                return cached;
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            cachedFields.put(clazz.getName() + ":" + field, f);
            return f;
        } catch (Throwable $ex) {
            try {
                throw $ex;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String field) {
        try {
            Method cached = cachedMethods.get(clazz.getName() + ":" + field);
            if (cached != null)
                return cached;
            Method f = clazz.getDeclaredMethod(field, new Class[0]);
            f.setAccessible(true);
            cachedMethods.put(clazz.getName() + ":" + field, f);
            return f;
        } catch (Throwable $ex) {
            try {
                throw $ex;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object invokeMethod(Method method, Object instance, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeMethod(Method method, Object instance) {
        try {
            return method.invoke(instance, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String field, Class<?>... paramaters) {
        try {
            Method cached = cachedMethods.get(clazz.getName() + ":" + field);
            if (cached != null)
                return cached;
            Method f = clazz.getDeclaredMethod(field, paramaters);
            f.setAccessible(true);
            cachedMethods.put(clazz.getName() + ":" + field, f);
            return f;
        } catch (Throwable $ex) {
            try {
                throw $ex;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void setObject(Object object, String fieldName, Object value) {
        try {
            Field field = getField(object.getClass(), fieldName);
            setObject(object, field, value);
        } catch (Throwable $ex) {
            throw $ex;
        }
    }

    public static void setObject(Object object, Field field, Object value) {
        try {
            if (field != null)
                field.set(object, value);
        } catch (Throwable $ex) {
            try {
                throw $ex;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object getObject(Class<?> clazz, Object instance, String fieldName) {
        try {
            Field f = getField(clazz, fieldName);
            return f.get(instance);
        } catch (Throwable $ex) {
            try {
                throw $ex;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Object callReflectionMethod(Object instance, Class<?> clazz, String method) {
        return callReflectionMethod(instance, clazz, method, null);
    }

    public static Object callReflectionMethod(@NonNull Object instance, String method) {
        if (instance == null)
            throw new NullPointerException("instance");
        return callReflectionMethod(instance, instance.getClass(), method, null);
    }

    public static Object callReflectionMethod(@NonNull Object instance, String method, ParamBuilder builder) {
        if (instance == null)
            throw new NullPointerException("instance");
        return callReflectionMethod(instance, instance.getClass(), method, builder);
    }

    public static Object callReflectionMethod(Object instance, Class<?> clazz, String method, ParamBuilder builder) {
        try {
            if (builder != null) {
                Class<?>[] params = new Class[builder.getParameters().size()];
                Object[] objcs = new Object[builder.getValidObjectCount()];
                int index = 0;
                for (RParam param : builder.getParameters()) {
                    int i = index++;
                    params[i] = param.getMethodClass();
                    objcs[i] = param.getInstance();
                }
                Method method1 = getMethod(clazz, method, params);
                return method1.invoke(instance, objcs);
            }
            Method m = getMethod(clazz, method);
            return m.invoke(instance, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNMSVersion() {
        return nmsVersion;
    }

    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getNMSVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Class<?> getOBCClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + getNMSVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Object getNMSHandle(Object obj) {
        try {
            return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
