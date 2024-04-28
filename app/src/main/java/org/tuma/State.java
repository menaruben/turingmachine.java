package org.tuma;

import java.util.List;

public class State {
    private final String name;
    private List<Transition> transitions;

    public State(String name) {
        this.name = name;
    }

    public void addTransition(Transition transition) {
        transitions.add(transition);
    }

    public String getName() {
        return name;
    }
}
