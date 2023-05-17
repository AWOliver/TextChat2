package Mane;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

//This class contains the usage of files in the chat
public class Rome {

    //creates a file and gives it content
    public void createFile(String fileName) {
        try {
            File rome = new File(fileName);
            FileWriter filewriter = new FileWriter(fileName);
            filewriter.write("The Metropolitan City of Rome, with a population of 4,355,725 residents, is the most populous metropolitan city in Italy.[3] Its metropolitan area is the third-most populous within Italy.");
            filewriter.close();
            if (rome.createNewFile()) {
                System.out.println("File created: " + fileName);
            } else {
                System.out.println("File already exists: " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    //used to read from a file
    public static String readFromFile(String file) {
                try {
                    return new String(Files.readAllBytes(Paths.get(file)));
                } catch (IOException e) {
                    System.out.println("Something went wrong: " + e.getMessage());
                    return "oops";
                }
            }
        }
