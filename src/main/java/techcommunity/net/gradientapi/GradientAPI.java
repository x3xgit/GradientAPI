package techcommunity.net.gradientapi;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GradientAPI {
    private static final Pattern[] GRADIENT_PATTERNS = {
            Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#([A-Fa-f0-9]{6})>"),
            Pattern.compile("\\{#([A-Fa-f0-9]{6})>}(.+?)\\{#([A-Fa-f0-9]{6})<}")
    };

    public static String applyAll(String text) {
        return applyGradient(applyChatColors(text));
    }

    public static String applyGradient(String textToTranslate) {
        for (Pattern pattern : GRADIENT_PATTERNS) {
            textToTranslate = applyGradientPattern(textToTranslate, pattern);
        }
        return textToTranslate;
    }

    public static String applyChatColors(String textToTranslate) {
        return ChatColor.translateAlternateColorCodes('&', textToTranslate);
    }

    private static String applyGradientPattern(String textToTranslate, Pattern pattern) {
        Matcher matcher = pattern.matcher(textToTranslate);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String startColor = "#" + matcher.group(1);
            String content = matcher.group(2);
            String endColor = "#" + matcher.group(3);

            if (isValidHexColor(startColor) && isValidHexColor(endColor)) {
                matcher.appendReplacement(sb, applyGradient(content, startColor, endColor));
            } else {
                matcher.appendReplacement(sb, content); // просто добавляем текст, если цвет недействителен
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String applyGradient(String text, String startColor, String endColor) {
        StringBuilder gradientText = new StringBuilder();
        int length = text.length();
        String plainText = text.replaceAll("(?i)" + ChatColor.COLOR_CHAR + "[0-9A-FK-OR]", "");
        int plainCharCount = plainText.length();
        int colorIndex = 0;
        String currentFormatting = "";

        for (int i = 0; i < length; i++) {
            if (text.charAt(i) == ChatColor.COLOR_CHAR) {
                currentFormatting += ChatColor.COLOR_CHAR + text.substring(i + 1, i + 2);
                i++; // пропускаем следующий символ, так как это часть кода цвета
            } else {
                float ratio = (float) colorIndex / (float) (plainCharCount - 1);
                String color = blendColors(startColor, endColor, ratio);
                gradientText.append(ChatColor.of(color)).append(currentFormatting).append(text.charAt(i));
                colorIndex++;
            }
        }
        return gradientText.toString();
    }

    private static String blendColors(String startColor, String endColor, float ratio) {
        int r1 = Integer.parseInt(startColor.substring(1, 3), 16);
        int g1 = Integer.parseInt(startColor.substring(3, 5), 16);
        int b1 = Integer.parseInt(startColor.substring(5, 7), 16);
        int r2 = Integer.parseInt(endColor.substring(1, 3), 16);
        int g2 = Integer.parseInt(endColor.substring(3, 5), 16);
        int b2 = Integer.parseInt(endColor.substring(5, 7), 16);

        int r = (int) (r1 * (1 - ratio) + r2 * ratio);
        int g = (int) (g1 * (1 - ratio) + g2 * ratio);
        int b = (int) (b1 * (1 - ratio) + b2 * ratio);

        return String.format("#%02x%02x%02x", r, g, b);
    }

    private static boolean isValidHexColor(String color) {
        return color.matches("#[A-Fa-f0-9]{6}");
    }
}
