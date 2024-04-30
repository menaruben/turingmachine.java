package org.tuma;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.ArrayList;

public class Turingmachine {
    private static final String initialState = "q1";
    private static final String acceptedStates = "q2";
    private int headPosition;
    private Map<String, List<Transition>> stateTransitions = new HashMap<>();
    private static final String BLANK_SYMBOL = "_";
    private List<String> readAlphabet;
    private int stepCounter = 0;
    private Map<String, Map<String, String>> memo = new HashMap<>();

    public Turingmachine(String code, List<String> readAlphabet) {
        this.readAlphabet = readAlphabet;
        codeToConfiguration(code);
    }

    // public Turingmachine(String flaciFilePath) {}

    private void codeToConfiguration(String code) {
        if (code.startsWith("1")) {
            code = code.substring(1);
        }

        String[] transitionCodes = code.split("11");
        Transition currentTransition;

        for (String transitionCode : transitionCodes) {
            currentTransition = new Transition(transitionCode, readAlphabet);

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
        sb.append("Initial state: ").append(initialState).append(System.lineSeparator());
        sb.append("Accepted states: ").append(acceptedStates).append(System.lineSeparator());
        sb.append("State transitions: \n").append(stateTransitionsToString()).append(System.lineSeparator());
        return sb.toString();
    }

    private Result<String, Verdict, String> doStep(String input, String currentState, Mode mode) {
        String readSymbol = input.substring(headPosition, headPosition + 1);
        Optional<Transition> nextTransition;

        if (mode == Mode.STEP) {
            System.out.println(formatCurrentState(input, currentState, headPosition));
        }

        if (stateTransitions.containsKey(currentState)) {
            nextTransition = stateTransitions.get(currentState)
                .stream()
                .filter(transition -> transition.getReadSymbol().equals(readSymbol))
                .findFirst();
        } else {
            nextTransition = Optional.empty();
        }

        if (nextTransition.isPresent()) {
            input = input.substring(0, headPosition)
                    + nextTransition.get().getWriteSymbol()
                    + input.substring(headPosition + 1);
            headPosition += nextTransition.get().getDirection().toInt();
            currentState = nextTransition.get().getToState();

            return doStep(input, currentState, mode);
        }

        Result<String, Verdict, String> result = new Result<>();
        result.setItem1(input);
        if ("q2".equals(currentState)) {
            result.setItem2(Verdict.ACCEPTED);
        } else {
            result.setItem2(Verdict.REJECTED);
        }
        result.setItem3(currentState);
        stepCounter = 0;
        return result;
    }

    private String formatCurrentState(String input, String currentState, int headPosition) {
        return String.format("%d: %s[%s]%s",
        stepCounter++, input.substring(0, headPosition), currentState, input.substring(headPosition));
    }

    public Result<String, Verdict, String> emulate(String input, int headPosition, Mode mode) {
        this.headPosition = headPosition;
        String transformedInput = BLANK_SYMBOL.repeat(15) + input + BLANK_SYMBOL.repeat(15);
        String currentState = initialState;
        return doStep(transformedInput, currentState, mode);
    }

    public enum Verdict {
        ACCEPTED, REJECTED
    }

    public enum Mode {
        STEP, SILENT
    }
}
