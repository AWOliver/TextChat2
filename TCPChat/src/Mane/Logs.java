package Mane;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
        public class Logs {
            private static String FILE_PATH;

            public static void writeToFile(String content, String FILE_PATH) {
                try {
                    FileWriter fileWriter = new FileWriter(FILE_PATH);
                    fileWriter.write(content);
                    fileWriter.close();
                    System.out.println("Message has been written to file.");
                } catch (IOException e) {
                    System.out.println("Something went wrong: " + e.getMessage());
                }
            }

            public static String readFromFile(String FILE_PATH) {
                try {
                    return new String(Files.readAllBytes(Paths.get(FILE_PATH)));
                } catch (IOException e) {
                    System.out.println("Something went wrong: " + e.getMessage());
                    return "";
                }
            }
        }
