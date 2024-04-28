package org.tuma;

import java.util.List;

public class Transition {
    private String fromState;
    private String toState;
    private String readSymbol;
    private String writeSymbol;
    private Transition.Direction direction;
    private List<String> readAlphabet;

    public Transition(String transitionCode, List<String> readAlphabet) {
        this.readAlphabet = readAlphabet;
        parseTransitionCode(transitionCode);
    }

    // 0   1    00  1   000  1  0        1  00
    // from     read    write   direction   to
    public void parseTransitionCode(String transitionCode) {
        String[] fields = transitionCode.split("1");
        this.fromState = "q" + fields[0].length();
        this.readSymbol = codeToReadSymbol(fields[1]);
        this.writeSymbol = WriteSymbol.fromString(fields[2]);
        this.direction = Direction.fromString(fields[3]);
        this.toState = "q" + fields[4].length();
    }

    private String codeToReadSymbol(String code) {
        return readAlphabet.get(code.length()-1);
    }

    public enum WriteSymbol {
        ZERO("0"), ONE("00"), BLANK("000");

        private final String code;
        WriteSymbol(String code) {
            this.code = code;
        }

        public static String fromString(String code) {
            switch (code) {
                case "0" -> { return "0"; }
                case "00" -> { return "1"; }
                case "000" -> { return "_"; }
                default -> { throw new IllegalArgumentException("Invalid write symbol code: " + code); }
            }
        }
    }

    public enum Direction {
        LEFT("0"), RIGHT("00");

        private final String code;
        Direction(String code) {
            this.code = code;
        }

        public static Transition.Direction fromString(String code) {
            return "0".equals(code) ? LEFT : RIGHT;
        }
    }

    public String toString() {
        return String.format("from: %s, to: %s, read: %s, write: %s, direction: %s\n",
        fromState, toState, readSymbol, writeSymbol, direction == Transition.Direction.LEFT ? "left" : "right");
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public String getReadSymbol() {
        return readSymbol;
    }

    public String getWriteSymbol() {
        return writeSymbol;
    }

    public Transition.Direction getDirection() {
        return direction;
    }
}