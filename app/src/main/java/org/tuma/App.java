package org.tuma;

import java.util.List;

import org.tuma.Turingmachine.Verdict;

public class App {

    public static void main(String[] args) {
        Turingmachine turingmachine = new Turingmachine(
            "010010001010011000101010010110001001001010011000100010001010",
            List.of("0", "1", "_"));
        Result<String, Verdict, String> result = turingmachine.emulate("11", 15, Turingmachine.Mode.STEP);
        System.out.println(result.toString());
    }
}
