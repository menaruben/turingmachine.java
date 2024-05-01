package org.tuma;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.HashMap;
import java.util.ArrayList;

public class Turingmachine {
    private static final String INITIAL_STATE = "q1";
    private static final String ACCEPTED_STATE = "q2";
    private int headPosition;
    private Map<String, List<Transition>> stateTransitions = new HashMap<>();
    private String blankSymbol;
    private int stepCounter = 0;
    private String currentState;
    private List<String> inputAlphabet;

    public Turingmachine(String code, List<String> inputAlphabet, List<String> writeAlphabet) {
        blankSymbol = writeAlphabet.get(2);
        this.inputAlphabet = inputAlphabet;
        codeToConfiguration(code, writeAlphabet);
    }

    // public Turingmachine(String filepath) {}

    private void codeToConfiguration(String code, List<String> writeAlphabet) {
        if (code.startsWith("1")) {
            code = code.substring(1);
        }

        String[] transitionCodes = code.split("11");
        Transition currentTransition;

        for (String transitionCode : transitionCodes) {
            currentTransition = new Transition(transitionCode, writeAlphabet);

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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d: ", stepCounter));

        ArrayList<String> paddedList = new ArrayList<>();
        paddedList.addAll(tape);
        paddedList.set(headPosition, currentState);
        paddedList.stream().forEach(sb::append);

        return sb.toString();
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
