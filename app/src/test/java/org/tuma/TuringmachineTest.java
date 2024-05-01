package org.tuma;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.tuma.Turingmachine.Verdict;

public class TuringmachineTest {
    @Test
    public void testEmulate_AcceptedInput() {
        Turingmachine turingmachine = new Turingmachine(
            "101001000000000100000100110000000001001000000000100000100110000000001000100010001011000100000100000000000001000010011000100000010000000000000100000001001100010000100010000101100010000000100010000000101100010001000010001001100000000000001000010000000000000100001001100000000000001001000000000000010010011000000000000010000000100000000000001000000010011000000000000010001000000000000001001011000000000000001001000000000000001001011000000000000001000010001000010110000100000001000010000001001100001000010000010000100110000000000001000010000000000001000001001100000000000010010001001011000001001000000100010110000010000100000001000010011000001000000010000000100000001001100000000001000010000000000010000101100000010000100100100110000000100001000000000010000101100000001001000000001001011000000001001000000001001011000000001000010000000010010110000000010000001000000001001011000000001000100100010011000000000001000010000000000001000000100",
            List.of("|", "TP", "_"),
            List.of("0","|","_","P","TP","$TP","$P"));

        Result<List<String>, Verdict, String> result = turingmachine.emulate(List.of(
            "|", "|", "|", "|", "|"), 0, Turingmachine.Mode.SILENT);

        assert(result.getItem2() == Verdict.ACCEPTED);
        assert(result.getItem1().stream().filter(e -> "|".equals(e)).count() == 25);
    }

    @Test
    public void testEmulate_RejectedInput() {
        Turingmachine turingmachine = new Turingmachine(
            "101001000000000100000100110000000001001000000000100000100110000000001000100010001011000100000100000000000001000010011000100000010000000000000100000001001100010000100010000101100010000000100010000000101100010001000010001001100000000000001000010000000000000100001001100000000000001001000000000000010010011000000000000010000000100000000000001000000010011000000000000010001000000000000001001011000000000000001001000000000000001001011000000000000001000010001000010110000100000001000010000001001100001000010000010000100110000000000001000010000000000001000001001100000000000010010001001011000001001000000100010110000010000100000001000010011000001000000010000000100000001001100000000001000010000000000010000101100000010000100100100110000000100001000000000010000101100000001001000000001001011000000001001000000001001011000000001000010000000010010110000000010000001000000001001011000000001000100100010011000000000001000010000000000001000000100",
            List.of("|", "TP", "_"),
            List.of("0","|","_","P","TP","$TP","$P"));

        Result<List<String>, Verdict, String> result = turingmachine.emulate(List.of(
            "|", "|", "|", "x", "|"), 0, Turingmachine.Mode.SILENT);

        assert(result.getItem2() == Verdict.REJECTED);
    }
}