package org.tuma;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ConfigReader {
    private static String readFile(String filepath) throws FileNotFoundException {
        try {
            File file = new File(filepath);
            Scanner scanner = new Scanner(file);
            String data = "";

            while (scanner.hasNextLine()) {
                data += scanner.nextLine();
            }
            scanner.close();
            return data;
        } catch (Exception e) {
            throw new FileNotFoundException("File not found");
        }
    }

    public static TuringmachineConfig readConfig(String filepath) throws Exception {
        String content = String.join("", Files.readAllLines(Path.of(filepath)));
        Gson gson = new Gson();
        return gson.fromJson(content, TuringmachineConfig.class);
    }
}
