package net.rupyber_studios.police_terminal.util;

import net.rupyber_studios.police_terminal.config.ModConfig;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Callsign {
    private static final Random RANDOM = new Random();
    private static final Pattern NUMBER_LETTER_NUMBER_PATTERN = Pattern.compile("^(\\d+)-(\\w+)-(\\d+)$");
    private static final Pattern NUMBER_LETTER_PATTERN = Pattern.compile("^(\\d+)-(\\w+)$");
    private static final Pattern LETTER_NUMBER_PATTERN = Pattern.compile("^(\\w+)-(\\d+)$");

    public static String createRandomCallsign() {
        ArrayList<String> types = new ArrayList<>();
        if(ModConfig.INSTANCE.callsignNumberLetter) types.add("N-L");
        if(ModConfig.INSTANCE.callsignLetterNumber) types.add("L-N");
        if(ModConfig.INSTANCE.callsignNumberLetterNumber || types.isEmpty()) types.add("N-L-N");
        short index = (short)(RANDOM.nextInt(0, types.size()));
        int firstNumber = RANDOM.nextInt(ModConfig.INSTANCE.callsignFirstNumberMin, ModConfig.INSTANCE.callsignFirstNumberMax + 1);
        short letterIndex = (short)(RANDOM.nextInt(0, ModConfig.INSTANCE.callsignLetters.size()));
        int secondNumber = RANDOM.nextInt(ModConfig.INSTANCE.callsignSecondNumberMin, ModConfig.INSTANCE.callsignSecondNumberMax + 1);
        return switch(types.get(index)) {
            case "N-L" -> firstNumber + "-" + ModConfig.INSTANCE.callsignLetters.get(letterIndex);
            case "L-N" -> ModConfig.INSTANCE.callsignLetters.get(letterIndex) + "-" + secondNumber;
            default -> firstNumber + "-" + ModConfig.INSTANCE.callsignLetters.get(letterIndex) + "-" + secondNumber;
        };
    }

    public static boolean isValid(String callsign) {
        Matcher matcher = NUMBER_LETTER_NUMBER_PATTERN.matcher(callsign);
        if(matcher.find()) {
            int firstNumber = Integer.parseInt(matcher.group(1));
            if(firstNumber < ModConfig.INSTANCE.callsignFirstNumberMin || firstNumber > ModConfig.INSTANCE.callsignFirstNumberMax)
                return false;

            String letter = matcher.group(2);
            if(!ModConfig.INSTANCE.callsignLetters.contains(letter))
                return false;

            int secondNumber = Integer.parseInt(matcher.group(3));
            if(secondNumber < ModConfig.INSTANCE.callsignSecondNumberMin || secondNumber > ModConfig.INSTANCE.callsignSecondNumberMax)
                return false;

            return ModConfig.INSTANCE.callsignNumberLetterNumber;
        }

        matcher = NUMBER_LETTER_PATTERN.matcher(callsign);
        if(matcher.find()) {
            int firstNumber = Integer.parseInt(matcher.group(1));
            if(firstNumber < ModConfig.INSTANCE.callsignFirstNumberMin || firstNumber > ModConfig.INSTANCE.callsignFirstNumberMax)
                return false;

            String letter = matcher.group(2);
            if(!ModConfig.INSTANCE.callsignLetters.contains(letter))
                return false;

            return ModConfig.INSTANCE.callsignNumberLetter;
        }

        matcher = LETTER_NUMBER_PATTERN.matcher(callsign);
        if(matcher.find()) {
            String letter = matcher.group(1);
            if(!ModConfig.INSTANCE.callsignLetters.contains(letter))
                return false;

            int secondNumber = Integer.parseInt(matcher.group(2));
            if(secondNumber < ModConfig.INSTANCE.callsignSecondNumberMin || secondNumber > ModConfig.INSTANCE.callsignSecondNumberMax)
                return false;

            return ModConfig.INSTANCE.callsignLetterNumber;
        }

        return false;
    }
}