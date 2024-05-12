package com.cmpe202.analyzers;

import com.cmpe202.interfaces.LogProcessor;
import com.cmpe202.processors.ApmLogProcessor;
import com.cmpe202.processors.AppLogProcessor;
import com.cmpe202.processors.RequestLogProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class LogAnalyzer {
    LogProcessor logProcessor;

    public void setLogProcessor(LogProcessor logProcessor) {
        this.logProcessor = logProcessor;
    }


    //Calling different strategy method based on logProcessor type
    public void analyzeLogs(List<String> logs) throws JsonProcessingException {
        logProcessor.processLog(logs);
    }
}
