package org.tuma;

import java.util.List;

public class TuringmachineConfig {
    private String goedelNumber;
    private List<String> inputAlphabet;
    private List<String> tapeAlphabet;

    public String getGoedelNumber() {
        return goedelNumber;
    }

    public void setGoedelNumber(String goedelNumber) {
        this.goedelNumber = goedelNumber;
    }

    public List<String> getInputAlphabet() {
        return inputAlphabet;
    }

    public void setInputAlphabet(List<String> inputAlphabet) {
        this.inputAlphabet = inputAlphabet;
    }

    public List<String> getTapeAlphabet() {
        return tapeAlphabet;
    }

    public void setTapeAlphabet(List<String> tapeAlphabet) {
        this.tapeAlphabet = tapeAlphabet;
    }
}
