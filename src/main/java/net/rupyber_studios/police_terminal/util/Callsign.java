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
        if(ModConfig.INSTANCE.callsignBeatUnit) types.add("B-U");
        if(ModConfig.INSTANCE.callsignUnitBeat) types.add("U-B");
        if(ModConfig.INSTANCE.callsignAreaUnitBeat || types.isEmpty()) types.add("A-U-B");
        short index = (short)(RANDOM.nextInt(0, types.size()));
        int area = RANDOM.nextInt(ModConfig.INSTANCE.callsignAreaMin, ModConfig.INSTANCE.callsignAreaMax + 1);
        short unitIndex = (short)(RANDOM.nextInt(0, ModConfig.INSTANCE.callsignUnits.size()));
        int beat = RANDOM.nextInt(ModConfig.INSTANCE.callsignBeatMin, ModConfig.INSTANCE.callsignBeatMax + 1);
        return switch(types.get(index)) {
            case "B-U" -> beat + "-" + ModConfig.INSTANCE.callsignUnits.get(unitIndex);
            case "U-B" -> ModConfig.INSTANCE.callsignUnits.get(unitIndex) + "-" + beat;
            default -> area + "-" + ModConfig.INSTANCE.callsignUnits.get(unitIndex) + "-" + beat;
        };
    }

    public static boolean isValid(String callsign) {
        Matcher matcher = NUMBER_LETTER_NUMBER_PATTERN.matcher(callsign);
        if(matcher.find()) {
            int area = Integer.parseInt(matcher.group(1));
            if(area < ModConfig.INSTANCE.callsignAreaMin || area > ModConfig.INSTANCE.callsignAreaMax)
                return false;

            String unit = matcher.group(2);
            if(!ModConfig.INSTANCE.callsignUnits.contains(unit))
                return false;

            int beat = Integer.parseInt(matcher.group(3));
            if(beat < ModConfig.INSTANCE.callsignBeatMin || beat > ModConfig.INSTANCE.callsignBeatMax)
                return false;

            return ModConfig.INSTANCE.callsignAreaUnitBeat;
        }

        matcher = NUMBER_LETTER_PATTERN.matcher(callsign);
        if(matcher.find()) {
            int beat = Integer.parseInt(matcher.group(1));
            if(beat < ModConfig.INSTANCE.callsignBeatMin || beat > ModConfig.INSTANCE.callsignBeatMax)
                return false;

            String unit = matcher.group(2);
            if(!ModConfig.INSTANCE.callsignUnits.contains(unit))
                return false;

            return ModConfig.INSTANCE.callsignBeatUnit;
        }

        matcher = LETTER_NUMBER_PATTERN.matcher(callsign);
        if(matcher.find()) {
            String unit = matcher.group(1);
            if(!ModConfig.INSTANCE.callsignUnits.contains(unit))
                return false;

            int beat = Integer.parseInt(matcher.group(2));
            if(beat < ModConfig.INSTANCE.callsignBeatMin || beat > ModConfig.INSTANCE.callsignBeatMax)
                return false;

            return ModConfig.INSTANCE.callsignUnitBeat;
        }

        return false;
    }
}