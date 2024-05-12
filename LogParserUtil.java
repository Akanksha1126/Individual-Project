package com.cmpe202.utility;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogParserUtil {

    public static List<String> parseInputFile(File file) throws IOException {

        List<String> fileContent = new ArrayList<>();

        // Create a Scanner object from the File object.
        BufferedReader reader = new BufferedReader(new FileReader(file));

        // Read and store each line in the list
        String line;
        while ((line = reader.readLine()) != null) {
            fileContent.add(line);
        }

        // Close the file
        reader.close();

        return  fileContent;
    }
}
