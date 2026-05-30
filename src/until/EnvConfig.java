package until;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EnvConfig {
    private static final Map<String, String> DOT_ENV = loadDotEnv();

    private EnvConfig() {
    }

    public static String get(String key) {
        String value = System.getenv(key);
        if (!isBlank(value)) {
            return value;
        }
        return DOT_ENV.get(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return isBlank(value) ? defaultValue : value;
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> values = new HashMap<>();
        Path envPath = Path.of(".env");

        if (!Files.exists(envPath)) {
            return values;
        }

        try {
            List<String> lines = Files.readAllLines(envPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                parseLine(line, values);
            }
        } catch (IOException e) {
            System.err.println(">> [Warning] Cannot read .env file.");
        }

        return values;
    }

    private static void parseLine(String line, Map<String, String> values) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }

        int equalsIndex = trimmed.indexOf('=');
        if (equalsIndex <= 0) {
            return;
        }

        String key = trimmed.substring(0, equalsIndex).trim();
        String value = trimmed.substring(equalsIndex + 1).trim();
        values.put(key, stripQuotes(value));
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
