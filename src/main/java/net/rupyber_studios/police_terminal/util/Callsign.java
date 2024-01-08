package net.rupyber_studios.police_terminal.util;

import net.rupyber_studios.police_terminal.config.ModConfig;

import java.util.ArrayList;
import java.util.Random;

public class Callsign {
    public static Random RANDOM = new Random();

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
}