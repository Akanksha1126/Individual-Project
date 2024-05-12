package com.cmpe202.processors;

import com.cmpe202.interfaces.LogProcessor;
import com.cmpe202.writer.OutputFileWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  RequestLogProcessor implements LogProcessor {

    public void processLog(List<String> logs) throws JsonProcessingException {

        Map<String, Map<String, Map<String, Long>>> requestLogOutputMap = new LinkedHashMap<>();
        Map<String, List<Long>> responseTimeMap = new LinkedHashMap<>();
        Map<String, Map<String, Long>> responseTimeMap1 = new LinkedHashMap<>();
        Map<String, Map<String, Long>> statusCodeMap1 = new LinkedHashMap<>();
      //  Map<String, Long> statusCodeMap = new LinkedHashMap<>();


        // Sample array of HTTP status codes
        int[] statusCodes = {200, 201, 404, 500, 503, 201, 500, 200, 201, 200, 600};

        // Define the regex pattern
        //String regex = "metric=(cpu_usage_percent|memory_usage_percent|disk_usage_percent) host=\\\\w+ value=(\\\\d+)";
        //String regex = "request_url=(\\w+).*?response_status=(\\d+).*?response_time_ms=(\\d+)";

        String regex = "request_url=\\\"(.*?)\\\"\\s+response_status=(\\d+)\\s+response_time_ms=(\\d+)";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        for (String line : logs) {
            // System.out.println(line);

            // Create a Matcher object
            Matcher matcher = pattern.matcher(line);

            // Check if the pattern is found in the line
            if (matcher.find()) {
                System.out.println("Pattern matched");
                //Extract key and value

                String request_url = matcher.group(1);
                int response_status = Integer.parseInt(matcher.group(2));
                Long response_time = Long.valueOf(matcher.group(3));

                System.out.println("responseTime : "+ response_time);

                List<Long> responseTimeList = responseTimeMap.getOrDefault(request_url, new ArrayList<>());
                responseTimeList.add(response_time);
                responseTimeMap.put(request_url, responseTimeList);

                Map<String, Long> statusCodeMap = statusCodeMap1.getOrDefault(request_url, new LinkedHashMap<>());
                //statusCodeMap.put("2XX", 0L);
                //statusCodeMap.put("4XX", 0L);
                //statusCodeMap.put("5XX", 0L);

                // Count the occurrences of each category
                if (response_status >= 200 && response_status < 300) {
                    statusCodeMap.put("2XX", statusCodeMap.getOrDefault("2XX", 0L) + 1); // Success
                }
                //if (response_status >= 300 && response_status < 400) {
                  //  statusCodeMap.put("3XX", statusCodeMap.getOrDefault("3XX", 0L) + 1); // Redirection
                //} else
                if (response_status >= 400 && response_status < 500) {
                    statusCodeMap.put("4XX", statusCodeMap.getOrDefault("4XX", 0L) + 1); // Client Error
                }

                if (response_status >= 500 && response_status < 600) {
                    statusCodeMap.put("5XX", statusCodeMap.getOrDefault("5XX", 0L) + 1); // Server Error
                }
                //else {
                  //  statusCodeMap.put("Other", statusCodeMap.getOrDefault("Other", 0L) + 1); // Other (out of range)
                //}

                statusCodeMap1.put(request_url, statusCodeMap);
            }

        }

        //Aggregate logs

        System.out.println("Response Time Map" + responseTimeMap);

        for(Entry<String, List<Long>> entry :  responseTimeMap.entrySet()) {
            List<Long> valueList = entry.getValue();
            Map<String, Long> responseTsMap = new LinkedHashMap<>();
            Collections.sort(valueList);

            //Calculate min, 50_percentile, 90_percentile, 95_percentile, 99_percentile, max

            long min = valueList.get(0);
            long max = valueList.get(valueList.size()-1);
            long percentile_50 = percentile(valueList, 50);
            long percentile_90 = percentile(valueList, 90);
            long percentile_95 = percentile(valueList, 95);
            long percentile_99 = percentile(valueList, 99);

            responseTsMap.put("min", min);
            responseTsMap.put("50_percentile", percentile_50);
            responseTsMap.put("90_percentile", percentile_90);
            responseTsMap.put("95_percentile", percentile_95);
            responseTsMap.put("99_percentile", percentile_99);
            responseTsMap.put("max", max);

            responseTimeMap1.put(entry.getKey(), responseTsMap);
        }

        System.out.println("Response Time Map" + responseTimeMap1);

        //write to output map

        for(Entry<String,Map<String, Long>> entry :  responseTimeMap1.entrySet()) {

            Map<String, Map<String, Long>> map1 = requestLogOutputMap.getOrDefault(entry.getKey(), new LinkedHashMap<>());
            map1.put("response_times",entry.getValue());

            requestLogOutputMap.put(entry.getKey(), map1);

        }

        for(Entry<String,Map<String, Long>> entry :  statusCodeMap1.entrySet()) {

            Map<String, Map<String, Long>> map1 = requestLogOutputMap.getOrDefault(entry.getKey(), new LinkedHashMap<>());
            map1.put("status_codes", entry.getValue());

            requestLogOutputMap.put(entry.getKey(), map1);
        }

        //convert to json
        String jsonString = covertToJson(requestLogOutputMap);

        //write to output file

        OutputFileWriter outputFileWriter = new OutputFileWriter();
        outputFileWriter.writeToFile(jsonString, "request.json");

    }

    public long percentile(List<Long> values, double percentile) {
        int index = (int) Math.ceil((percentile / 100) * values.size());
        return values.get(index - 1);
    }

    public String covertToJson(Map<String, Map<String, Map<String, Long>>> requestLogOutputMap) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // String jsonString = objectMapper.writeValueAsString(apmOutputMap);
        String prettyJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestLogOutputMap);

        System.out.println(prettyJsonString);
        return  prettyJsonString;

    }


}
