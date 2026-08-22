package com.github.alexthe666.alexsmobs.config;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ConfigHolder {

    private static final String FILE_NAME = "alexsmobs.toml";

    private ConfigHolder() {
    }

    /** Load defaults, overlay from config/alexsmobs.toml, then write the file if missing. */
    public static void loadConfig() {
        CommonConfig config = new CommonConfig();
        AMConfig.bake(config);
        Path file = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        try {
            if (Files.exists(file)) {
                overlayFromToml(Files.readString(file, StandardCharsets.UTF_8));
            } else {
                Files.createDirectories(file.getParent());
                Files.writeString(file, writeToml(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            AlexsMobs.LOGGER.warn("Could not load or write {}", FILE_NAME, e);
        }
    }

    public static boolean save() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, writeToml(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            AlexsMobs.LOGGER.warn("Could not save {}", FILE_NAME, e);
            return false;
        }
    }

    private static String writeToml() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Alex's Mobs common config\n");
        sb.append("# Set a spawn weight to 0 to disable that mob's natural spawning.\n");
        sb.append("# Spawn biome lists live in config/alexsmobs/*.json after first launch.\n");
        sb.append("[general]\n");
        for (Field field : AMConfig.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            try {
                Object value = field.get(null);
                sb.append(field.getName()).append(" = ").append(formatValue(value)).append('\n');
            } catch (IllegalAccessException ignored) {
            }
        }
        return sb.toString();
    }

    private static String formatValue(Object value) {
        if (value instanceof String s) {
            return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
        }
        if (value instanceof List<?> list) {
            StringBuilder inner = new StringBuilder("[");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    inner.append(", ");
                }
                inner.append(formatValue(list.get(i)));
            }
            return inner.append(']').toString();
        }
        return String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private static void overlayFromToml(String text) {
        for (String raw : text.split("\\R")) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("[")) {
                continue;
            }
            int eq = line.indexOf('=');
            if (eq <= 0) {
                continue;
            }
            String key = line.substring(0, eq).trim();
            String rawValue = line.substring(eq + 1).trim();
            try {
                Field field = AMConfig.class.getDeclaredField(key);
                if (!Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type == boolean.class || type == Boolean.class) {
                    field.set(null, Boolean.parseBoolean(rawValue));
                } else if (type == int.class || type == Integer.class) {
                    field.set(null, Integer.parseInt(stripNumber(rawValue)));
                } else if (type == double.class || type == Double.class) {
                    field.set(null, Double.parseDouble(stripNumber(rawValue)));
                } else if (type == float.class || type == Float.class) {
                    field.set(null, Float.parseFloat(stripNumber(rawValue)));
                } else if (type == String.class) {
                    field.set(null, unquote(rawValue));
                } else if (List.class.isAssignableFrom(type)) {
                    field.set(null, parseStringList(rawValue));
                }
            } catch (NoSuchFieldException ignored) {
            } catch (Exception e) {
                AlexsMobs.LOGGER.warn("Skipping invalid config key {}", key);
            }
        }
    }

    private static String stripNumber(String raw) {
        String s = raw.trim();
        if (s.endsWith("D") || s.endsWith("d") || s.endsWith("F") || s.endsWith("f")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static String unquote(String raw) {
        String s = raw.trim();
        if (s.length() >= 2 && ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")))) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static List<String> parseStringList(String raw) {
        String s = raw.trim();
        List<String> out = new ArrayList<>();
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        if (s.isEmpty()) {
            return out;
        }
        for (String part : s.split(",")) {
            String item = unquote(part.trim());
            if (!item.isEmpty()) {
                out.add(item);
            }
        }
        return out;
    }

    public static String describeFile() {
        return FILE_NAME.toLowerCase(Locale.ROOT);
    }
}
