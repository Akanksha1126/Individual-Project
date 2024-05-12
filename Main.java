package com.cmpe202;

import com.cmpe202.analyzers.LogAnalyzer;
import com.cmpe202.processors.ApmLogProcessor;
import com.cmpe202.processors.AppLogProcessor;
import com.cmpe202.processors.RequestLogProcessor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.cmpe202.utility.LogParserUtil.parseInputFile;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("hello");

        try {

            if (args.length != 2 || !args[0].equals("--file")) {
                System.err.println("Usage: java LogAnalyzer --file <filename>");
                System.exit(1);
            }

            System.out.println("Filename -" + args[1]);

            //Store the filename
            String filename = args[1];
            System.out.println("Filename -" + filename);
            if (filename == null) {
                System.err.println("File is invalid");
                System.exit(1);
            } else {
                File file = new File(filename);
                List<String> logs = parseInputFile(file);

                LogAnalyzer logAnalyzer = new LogAnalyzer();

                //Set strategy based on Log Processor type

                //Call APM Log Strategy

                System.out.println("Analyzing APM Log processor");
                logAnalyzer.setLogProcessor(new ApmLogProcessor());
                logAnalyzer.analyzeLogs(logs);

                //Call App Log Strategy
                logAnalyzer.setLogProcessor(new AppLogProcessor());
                logAnalyzer.analyzeLogs(logs);

                //Call Request Log Strategy
                logAnalyzer.setLogProcessor(new RequestLogProcessor());
                logAnalyzer.analyzeLogs(logs);
            }
        }

        catch(Exception e) {
            System.out.println(e);

        }
    }

}