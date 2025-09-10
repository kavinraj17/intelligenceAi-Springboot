package org.example.intelligenceaiapplication.service;

import org.springframework.stereotype.Service;

@Service
public class BugPredictionService {

    public String predictSeverity(String description){
        String lower = description.toLowerCase();

        if(lower.contains("null"))
            return "High";
        else if(lower.contains("slow")||lower.contains("performance"))
            return "Medium";
        else return "Low";
    }
}
