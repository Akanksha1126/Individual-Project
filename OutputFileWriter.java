package com.cmpe202.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class OutputFileWriter {

    public void writeToFile(String content, String filename) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Write each line from the list to the file
                writer.write(content);
                writer.newLine(); // Add a newline character after each line
            } catch (IOException ex) {
                throw new RuntimeException(ex);
        }
            System.out.println("File '" + filename + "' has been written successfully.");
        }
 }
