package org.tuma;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Turingmachine {
    private final String initialState = "q1";
    private final String acceptedStates = "q2";
    private int headPosition;
    private Map<String, List<Transition>> stateTransitions = new HashMap<>();
    private static final String BLANK_SYMBOL = "_";
    private List<String> readAlphabet;

    public Turingmachine(String code, List<String> readAlphabet) {
        this.readAlphabet = readAlphabet;
        codeToConfiguration(code);
    }

    private void codeToConfiguration(String code) {
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

        for (String state : stateTransitions.keySet()) {
            for (Transition transition : stateTransitions.get(state)) {
                sb.append(transition.toString());
            }
        }

        return sb.toString();
    }

    public String toString() {
        return "Turingmachine configuration:" + "\n" +
                "Initial state: " + initialState + "\n" +
                "Accepted states: " + acceptedStates + "\n" +
                "State transitions: \n" + stateTransitionsToString() + "\n";
    }

    private Result<String, Verdict, String> doStep(String input, String currentState, Mode mode) {
        String readSymbol = input.substring(headPosition, headPosition + 1);
        Transition nextTransition;

        if (mode == Mode.STEP) {
            System.out.println(formatCurrentState(input, currentState, headPosition));
        }

        if (nextTransitionExists(currentState, readSymbol)) {
            nextTransition = stateTransitions.get(currentState).stream()
                    .filter(transition -> transition.getReadSymbol().equals(readSymbol))
                    .findFirst()
                    .get();

            input = input.substring(0, headPosition) + nextTransition.getWriteSymbol() +
                    input.substring(headPosition + 1);
            headPosition += nextTransition.getDirection() == Transition.Direction.RIGHT ? 1 : -1;
            currentState = nextTransition.getToState();

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
        return result;
    }

    private String formatCurrentState(String input, String currentState, int headPosition) {
        return input.substring(0, headPosition) + "[" + currentState + "]" + input.substring(headPosition);
    }

    private boolean nextTransitionExists(String currentState, String readSymbol) {
        return stateTransitions.containsKey(currentState)
                && stateTransitions.get(currentState).stream()
                    .anyMatch(transition -> transition.getReadSymbol().equals(readSymbol));
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
