package me.huaisu.common.android.utils;


import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtils {

    private static final String TAG = "ReflectUtils";

    public static void printAllMethods(Class cls) {
        Logger.i(TAG, "Class " + cls.getName() + " isInterface " + cls.isInterface() + " methods:");
        Method[] methods = cls.getDeclaredMethods();
        if (methods != null) {
            for (Method m : methods) {
                Logger.i(TAG, "declared method " + m.getName() + " args:" + arrayToString(m.getParameterTypes()));
            }
        }
        methods = cls.getMethods();
        if (methods != null) {
            for (Method m : methods) {
                Logger.i(TAG, "method " + m.getName() + " args:" + arrayToString(m.getParameterTypes()));
            }
        }
    }

    public static String arrayToString(Object[] array) {
        StringBuilder builder = new StringBuilder();
        if (array != null && array.length > 0) {
            builder.append("[");
            for (Object obj : array) {
                builder.append(obj.toString() + ",");
            }
            builder.append("]");
        }
        return builder.toString();
    }

    public static <T> T getField(Object obj, String fieldName) {
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        if (field == null) {
            try {
                field = obj.getClass().getField(fieldName);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        if (field != null) {
            try {
                field.setAccessible(true);
                return (T) field.get(obj);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        return null;
    }

    public static <T> void setField(Object obj, String fieldName, T value) {
        Field field = null;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        if (field == null) {
            try {
                field = obj.getClass().getField(fieldName);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, value);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
    }

    public static <T> T getStaticField(Class<?> clz, String fieldName) {
        Field field = null;
        try {
            field = clz.getDeclaredField(fieldName);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        if (field == null) {
            try {
                field = clz.getField(fieldName);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        if (field != null) {
            try {
                field.setAccessible(true);
                return (T) field.get(null);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        return null;
    }

    public static <T> void setStaticField(Class<?> clz, String fieldName, T value) {
        Field field = null;
        try {
            field = clz.getDeclaredField(fieldName);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        if (field == null) {
            try {
                field = clz.getField(fieldName);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(null, value);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
    }

    public static <T> T invokeMethod(Object obj, String methodName) {
        Method method = null;
        try {
            method = obj.getClass().getDeclaredMethod(methodName);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        if (method == null) {
            try {
                method = obj.getClass().getMethod(methodName);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        if (method != null) {
            try {
                method.setAccessible(true);
                return (T) method.invoke(obj);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        return null;
    }

    public static <T> T invokeStaticMethod(Class<?> clz, String methodName) {
        Method method = null;
        try {
            method = clz.getDeclaredMethod(methodName);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
        if (method == null) {
            try {
                method = clz.getMethod(methodName);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        if (method != null) {
            try {
                method.setAccessible(true);
                return (T) method.invoke(null);
            } catch (Exception e) {
                Logger.e(TAG, e);
            }
        }
        return null;
    }
}
