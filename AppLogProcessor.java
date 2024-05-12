package com.cmpe202.processors;

import com.cmpe202.interfaces.LogProcessor;
import com.cmpe202.writer.OutputFileWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppLogProcessor implements LogProcessor {

    public void processLog(List<String> logs) throws JsonProcessingException {
        Map<String, Long> appLogMap = new LinkedHashMap<>();

        System.out.println("Analyzing Application Log Processor");


        // Define the regex pattern
        //String regex = "metric=(cpu_usage_percent|memory_usage_percent|disk_usage_percent) host=\\\\w+ value=(\\\\d+)";
        String regex = "level=(\\w+)";
        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        for(String line : logs) {

            // Create a Matcher object
            Matcher matcher = pattern.matcher(line);

            // Check if the pattern is found in the line
            if(matcher.find()) {
                System.out.println("Pattern matched");
                //Extract key and value

                String key = matcher.group(1);

                System.out.println("Key=" + key);
                //Store in map
                appLogMap.put(key, appLogMap.getOrDefault(key, 0L)+1);
            }

            System.out.println(appLogMap);
        }

        String jsonString = covertToJson(appLogMap);

        OutputFileWriter outputFileWriter = new OutputFileWriter();
        outputFileWriter.writeToFile(jsonString, "application.json");
    }


    public String covertToJson(Map<String, Long> appOutputMap) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // String jsonString = objectMapper.writeValueAsString(apmOutputMap);
        String prettyJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(appOutputMap);

        System.out.println(prettyJsonString);
        return  prettyJsonString;

    }
}
