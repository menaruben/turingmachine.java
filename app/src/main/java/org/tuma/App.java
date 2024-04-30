package org.tuma;

import java.util.List;

import org.tuma.Turingmachine.Verdict;

public class App {

    public static void main(String[] args) {
        Turingmachine turingmachine = new Turingmachine(
            "1010010100100110101000101001100010010100100110001010010100",
            List.of("0", "1", "_"),
            List.of("0", "1", "_"));
        Result<String, Verdict, String> result = turingmachine.emulate("111110110", 15, Turingmachine.Mode.STEP);
        System.out.println(result.toString());
    }
}
