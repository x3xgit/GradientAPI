package techcommunity.gradient;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradientAPI {
    private static final Pattern[] GRADIENT_PATTERNS = {
            Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#([A-Fa-f0-9]{6})>"),
            Pattern.compile("\\{#([A-Fa-f0-9]{6})>}(.+?)\\{#([A-Fa-f0-9]{6})<}")
    };

    public static String applyAll(String text)
    {
        return applyGradient(translateAlternateColorCodes('&', text));
    }

    public static String applyGradient(String textToTranslate)
    {
        for (Pattern pattern : GRADIENT_PATTERNS)
            textToTranslate = applyGradientPattern(textToTranslate, pattern);

        return textToTranslate;
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate)
    {
        return ChatColor.translateAlternateColorCodes(altColorChar, textToTranslate);
    }

    private static String applyGradientPattern(String textToTranslate, Pattern pattern)
    {
        Matcher matcher = pattern.matcher(textToTranslate);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String color = matcher.group(1);
            String content = matcher.group(2);
            matcher.appendReplacement(sb, applyColor(content, color));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String applyColor(String text, String color) {
        StringBuilder coloredText = new StringBuilder();
        for (char c : text.toCharArray()) {
            coloredText.append(ChatColor.of(color)).append(c);
        }
        return coloredText.toString();
    }
}