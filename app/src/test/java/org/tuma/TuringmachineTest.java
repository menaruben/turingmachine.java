package org.tuma;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.tuma.Turingmachine.Verdict;
import com.google.gson.Gson;

public class TuringmachineTest {
    @Test
    public void testEmulate_Example1() {
        Turingmachine turingmachine = new Turingmachine(
            "010010001010011000101010010110001001001010011000100010001010",
            List.of("0", "1", "_"),
            List.of("0", "1", "_"));

        Result<List<String>, Verdict, String> result = turingmachine.emulate(List.of(
            "1", "1"), 0, Turingmachine.Mode.STEP);

        assert(result.getItem2() == Verdict.ACCEPTED);
    }

    @Test
    public void testEmulate_Example2() {
        Turingmachine turingmachine = new Turingmachine(
            "1010010100100110101000101001100010010100100110001010010100",
            List.of("0", "1", "_"),
            List.of("0", "1", "_"));

        Result<List<String>, Verdict, String> result = turingmachine.emulate(List.of(
            "1", "1", "1", "1", "0", "1", "0", "0"), 0, Turingmachine.Mode.STEP);

        assert(result.getItem2() == Verdict.ACCEPTED);
    }

    @Test
    public void testEmulate_Example3() {
        Turingmachine turingmachine = new Turingmachine(
            "10100010001010011010010100100110001001000100100110001000100001001011000010010000100101100001010000010101100000100001000001000010110000010010000001000010011000001000100000000100010011000000100001000000100001001100000010100000010100110000001001000000100100110000001000100000001001011000000010010000100101100000001010000101011000000010000100001000010011000000001001000000001001001100000000101000000001010011000000001000010000000010010011000000001000100000000010001011000000000100100000000000000000000101011000000000010010000000000010001001100000000001000100000000001000100110000000000101000000000000100010011000000000001010000000000000101001100000000000100100000000000100100110000000000000101000000000000001010110000000000000100100000000000000001000010011000000000000100100000000000010001001100000000000010100100010011000000000000001000010000000000000010010110000000000000010100000000000000010101100000000000000010001000000000010001001100000000000000010010000000000000001001011000000000000000010010000000000000000100100110000000000000000101000000000000000001010011000000000000000001001000000000000000001001001100000000000000000100010000000000000000001001011000000000000000000100100000000000000000010010110000000000000000001010000000000000000000101011000000000000000000010000100000000000001000010011000000000000000000010010000000000000000000100101100000000000000000000100100000000000000000000100101100000000000000000000101000000000000000000001010110000000000000000000010000100000000000000000000100001011000000000000000000001000100000000001000100",
            List.of("0", "|", "_"),
            List.of("0", "|", "_", "x"));

        Result<List<String>, Verdict, String> result = turingmachine.emulate(
            List.of(
                "|", "|", "|", "|",
                "|", "|", "|", "|",
                "|", "|", "|", "|",
                "|", "|", "|", "|",
                "|", "|", "|", "|",
                "|", "|", "|", "|",
                "|", "|", "|", "|",
                "|", "|", "|", "|"),
            0, Turingmachine.Mode.SILENT);

        assert(result.getItem2() == Verdict.ACCEPTED);
        assert(result.getItem1().stream().filter(e -> "|".equals(e)).count() == 1024);
    }

    @Test
    public void testEmulate_Example4() {
        TuringmachineConfig tc;
        try {
            tc = ConfigReader.readConfig("D:\\ZHAW\\Y1S2\\THIN\\Serie4\\turingmachine.java\\app\\src\\main\\resources\\config1.json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Turingmachine turingmachine = new Turingmachine(tc.getGoedelNumber(), tc.getInputAlphabet(), tc.getTapeAlphabet());
        Result<List<String>, Verdict, String> result = turingmachine.emulate(List.of(
                "1", "1", "1", "1", "0", "1", "0", "0"), 0, Turingmachine.Mode.SILENT);

        assert(result.getItem2() == Verdict.ACCEPTED);
        System.out.println(result.toString());
    }
}
