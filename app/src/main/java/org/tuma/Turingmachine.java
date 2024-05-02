package org.tuma;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

public class Turingmachine {
    private static final String INITIAL_STATE = "q1";
    private static final String ACCEPTED_STATE = "q2";
    private int headPosition;
    private Map<String, List<Transition>> stateTransitions = new HashMap<>();
    private final String blankSymbol;
    private int stepCounter = 0;
    private String currentState;
    private final List<String> inputAlphabet;

    public Turingmachine(String goedelNumber, List<String> inputAlphabet, List<String> tapeAlphabet) {
        blankSymbol = tapeAlphabet.get(2);
        this.inputAlphabet = inputAlphabet;
        goedelNumberToConfig(goedelNumber, tapeAlphabet);
    }

    private void goedelNumberToConfig(String goedelNumber, List<String> tapeAlphabet) {
        if (goedelNumber.startsWith("1")) {
            goedelNumber = goedelNumber.substring(1);
        }

        String[] transitionCodes = goedelNumber.split("11");
        Transition currentTransition;

        for (String transitionCode : transitionCodes) {
            currentTransition = new Transition(transitionCode, tapeAlphabet);

            if (!stateTransitions.containsKey(currentTransition.getFromState())) {
                stateTransitions.put(currentTransition.getFromState(), new ArrayList<>());
                stateTransitions.get(currentTransition.getFromState()).add(currentTransition);
            } else {
                stateTransitions.get(currentTransition.getFromState()).add(currentTransition);
            }
        }
    }

    private String stateTransitionsToString() {
        StringBuilder sb = new StringBuilder();

        stateTransitions.keySet().stream()
            .forEach(state -> stateTransitions.get(state).stream()
                .forEach(transition -> sb.append(transition.toString())));

        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Turingmachine configuration:").append(System.lineSeparator());
        sb.append("Initial state: ").append(INITIAL_STATE).append(System.lineSeparator());
        sb.append("Accepted states: ").append(ACCEPTED_STATE).append(System.lineSeparator());
        sb.append("State transitions: \n").append(stateTransitionsToString()).append(System.lineSeparator());
        return sb.toString();
    }

    private boolean tryNextTransition(List<String> tape, String readSymbol) {
        if (!stateTransitions.containsKey(currentState)) {
            return false;
        }

        Optional<Transition> nextTransition = stateTransitions.get(currentState)
                .stream()
                .filter(transition -> transition.getReadSymbol().equals(readSymbol))
                .findFirst();

        if (nextTransition.isPresent()) {
            tape.set(headPosition, nextTransition.get().getWriteSymbol());
            headPosition += nextTransition.get().getDirection().toInt();
            currentState = nextTransition.get().getToState();
            return true;
        }

        return false;
    }

    private List<String> checkHeadPositionForBounds(List<String> tape) {
        if (headPosition == tape.size()) {
            tape.add(blankSymbol);
        }

        if (headPosition == -1) {
            headPosition = 0;
            tape.add(0, blankSymbol);
        }

        return tape;
    }

    private void printCurrentState(List<String> tape) {
        System.out.println(formatCurrentState(tape, headPosition));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Result<List<String>, Verdict, String> executeTransitions(List<String> tape, Mode mode) {
        Result<List<String>, Verdict, String> result = new Result<>();
        String readSymbol;
        do {
            tape = checkHeadPositionForBounds(tape);
            readSymbol = tape.get(headPosition);

            if (mode == Mode.STEP) {
                printCurrentState(tape);
                stepCounter++;
            }
        } while(tryNextTransition(tape, readSymbol));

        result.setItem1(tape);
        if ("q2".equals(currentState)) {
            result.setItem2(Verdict.ACCEPTED);
        } else {
            result.setItem2(Verdict.REJECTED);
        }
        result.setItem3(currentState);
        stepCounter = 0;

        return result;
    }

    private String formatCurrentState(List<String> tape, int headPosition) {
        int paddingSize = 15;
        List<String> paddedList = new ArrayList<>(Collections.nCopies(tape.size() + 2 * paddingSize, "_"));
        for (int i = 0; i < tape.size(); i++) {
            paddedList.set(i + paddingSize, tape.get(i));
        }
        paddedList.add(headPosition + paddingSize, "[" + currentState + "]");
        return String.join(" ", paddedList);
    }

    private boolean isValidInput(List<String> tape) {
        return tape.stream().allMatch(symbol -> inputAlphabet.contains(symbol));
    }

    public Result<List<String>, Verdict, String> emulate(List<String> inputWord, int headPosition, Mode mode) {
        if (!isValidInput(inputWord)) {
            Result<List<String>, Verdict, String> result = new Result<>();
            result.setItem1(inputWord);
            result.setItem2(Verdict.REJECTED);
            result.setItem3("Invalid input word, no end state.");
            return result;
        }

        this.headPosition = headPosition;
        List<String> tape = new ArrayList<>();
        tape.addAll(inputWord);
        currentState = INITIAL_STATE;
        return executeTransitions(tape, mode);
    }

    public enum Verdict {
        ACCEPTED, REJECTED
    }

    public enum Mode {
        STEP, SILENT
    }
}
