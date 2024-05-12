package com.cmpe202.processors;

import com.cmpe202.interfaces.LogProcessor;
import com.cmpe202.writer.OutputFileWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApmLogProcessor implements LogProcessor {

    public void processLog(List<String> logs) throws JsonProcessingException {

        System.out.println("Analyzing APM Log Processor");

        Map<String, List<Double>> apmLogMap= new HashMap<>();

        // Define the regex pattern
        //String regex = "metric=(cpu_usage_percent|memory_usage_percent|disk_usage_percent) host=\\\\w+ value=(\\\\d+)";
        String regex = "metric=(\\w+).*?value=(\\d+)";
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
                Double value = Double.parseDouble(matcher.group(2));

                List<Double> valueList = apmLogMap.getOrDefault(key,new ArrayList<>());
                valueList.add(value);
                //Store in map
                apmLogMap.put(key, valueList);
            }

            System.out.println(apmLogMap);
        }

        apmLogAggregrator(apmLogMap);
    }

    public void apmLogAggregrator(Map<String, List<Double>> apmLogMap) throws JsonProcessingException {
        //Iterate through the map
        Map<String, Map<String, Double>> apmOutputMap = new HashMap<>();
        for(Map.Entry<String, List<Double>> entry : apmLogMap.entrySet()) {
            Map<String, Double> apmAggregrateMap = new LinkedHashMap<>();
            List<Double> valueList = entry.getValue();

            //Find minimum
            Double min = Collections.min(valueList);

            //Find maximum;
            Double max = Collections.max(valueList);

            // Calculate median
            Collections.sort(valueList);
            int size = valueList.size();

            Double median = (double) 0;
            if (size % 2 == 0) {
                median = (valueList.get(size / 2 - 1) + valueList.get(size / 2)) / 2;
            } else {
                median = valueList.get(size / 2);
            }

            // Calculate average
            double sum = 0;
            for (double num : valueList) {
                sum += num;
            }
            double average = sum / valueList.size();

            apmAggregrateMap.put("minimum", min);
            apmAggregrateMap.put("median", median);
            apmAggregrateMap.put("average", average);
            apmAggregrateMap.put("max", max);

            apmOutputMap.put(entry.getKey(), apmAggregrateMap);
        }

        //convert to json string
        String prettyJsonString = covertToJson(apmOutputMap);

        //Write to output file

        OutputFileWriter outputFileWriter = new OutputFileWriter();
        outputFileWriter.writeToFile(prettyJsonString, "apm.json");
    }

    public String covertToJson(Map<String, Map<String, Double>> apmOutputMap) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
       // String jsonString = objectMapper.writeValueAsString(apmOutputMap);
        String prettyJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apmOutputMap);

        System.out.println(prettyJsonString);
        return  prettyJsonString;

    }
}
