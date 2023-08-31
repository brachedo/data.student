package com.grandp.data.entity.enumerated;

import com.grandp.data.exception.notfound.runtime.SemesterNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Semester {
    NONE("NONE", 0),
    FIRST("FIRST", 1),
    SECOND("SECOND", 2),
    THIRD("THIRD", 3),
    FOURTH("FOURTH", 4),
    FIFTH("FIFTH", 5),
    SIXTH("SIXTH", 6),
    SEVENTH("SEVENTH", 7),
    EIGHTH("EIGHTH", 8);

    private final String value;
    private static final List<String> list = Arrays.stream(Semester.values()).map(Semester::name).collect(Collectors.toList());
    private final int intValue;
    private static final List<Integer> intList = Arrays.stream(Semester.values()).map(Semester::getIntValue).collect(Collectors.toList());

    Semester(String value, int intValue) {
        this.value = value;
        this.intValue = intValue;
    }

    public static Semester of(@NotNull String semester) throws SemesterNotFoundException {
        return switch (semester.toUpperCase()) {
            case "FIRST" -> FIRST;
            case "SECOND" -> SECOND;
            case "THIRD" -> THIRD;
            case "FOURTH" -> FOURTH;
            case "FIFTH" -> FIFTH;
            case "SIXTH" -> SIXTH;
            case "SEVENTH" -> SEVENTH;
            case "EIGHTH" -> EIGHTH;
            default -> throw new SemesterNotFoundException("Semester '" + semester + "' does not exist.");
        };
    }

    public static Semester of(@NotNull int semester) throws SemesterNotFoundException {
        return switch (semester) {
            case 1 -> FIRST;
            case 2 -> SECOND;
            case 3 -> THIRD;
            case 4 -> FOURTH;
            case 5 -> FIFTH;
            case 6 -> SIXTH;
            case 7 -> SEVENTH;
            case 8 -> EIGHTH;
            default -> throw new SemesterNotFoundException("Semester '" + semester + "' does not exist.");
        };
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }

    public static List<String> getValues() {
        return new ArrayList<>(list);
    }
}