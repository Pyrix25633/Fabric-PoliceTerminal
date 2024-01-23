package net.rupyber_studios.police_terminal.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class Credentials {
    private static final Random RANDOM = new Random();
    public static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    public static final char[] LETTERS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                                                    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                                                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                                                    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static final char[] SYMBOLS = new char[]{'-', '_', '.', '*', '/', '@', '#'};

    public static @NotNull String generatePassword() {
        ArrayList<Character> chars = new ArrayList<>();
        chars.add(DIGITS[RANDOM.nextInt(DIGITS.length)]);
        chars.add(SYMBOLS[RANDOM.nextInt(SYMBOLS.length)]);
        for(int i = 0; i < 6; i++) {
            char c = switch(RANDOM.nextInt(3)) {
                case 0 -> DIGITS[RANDOM.nextInt(DIGITS.length)];
                case 1 -> LETTERS[RANDOM.nextInt(LETTERS.length)];
                default -> SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
            };
            chars.add(c);
        }
        StringBuilder password = new StringBuilder();
        while(!chars.isEmpty())
            password.append(chars.remove(RANDOM.nextInt(chars.size())));
        return password.toString();
    }

    public static @NotNull String generateToken() {
        ArrayList<Character> chars = new ArrayList<>();
        for(int i = 0; i < 16; i++) {
            char c = switch(RANDOM.nextInt(3)) {
                case 0 -> DIGITS[RANDOM.nextInt(DIGITS.length)];
                case 1 -> LETTERS[RANDOM.nextInt(LETTERS.length)];
                default -> SYMBOLS[RANDOM.nextInt(SYMBOLS.length)];
            };
            chars.add(c);
        }
        StringBuilder token = new StringBuilder();
        while(!chars.isEmpty())
            token.append(chars.remove(RANDOM.nextInt(chars.size())));
        return token.toString();
    }
}