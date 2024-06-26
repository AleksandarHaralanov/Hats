package com.haralanov.utilities;

public class ColorUtil {

    /**
     * Translates alternate color codes in the given text to Minecraft color codes.
     *
     * @param altColorChar The character used to denote color codes '&'.
     * @param textToTranslate The text containing the alternate color codes.
     * @return The text with the alternate color codes replaced by Minecraft color codes.
     */
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] charArray = textToTranslate.toCharArray();
        for (int i = 0; i < charArray.length - 1; i++) {
            if (charArray[i] == altColorChar && "0123456789AaBbCcDdEeFf".indexOf(charArray[i + 1]) > -1) {
                charArray[i] = '\u00A7';
                charArray[i + 1] = Character.toLowerCase(charArray[i + 1]);
            }
        }
        return new String(charArray);
    }
}
