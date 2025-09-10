package org.example.intelligenceaiapplication.dto;

public class BugPredictionResponse {
    public String description;
    public String predictSeverity;

    public BugPredictionResponse(String description,String predictSeverity){
        this.description = description;
        this.predictSeverity = predictSeverity;
    }

    public String getDescription() {
        return description;
    }
    public String getPredictSeverity(){
        return predictSeverity;
    }

    public void setPredictSeverity(String PredictSeverity){
        this.predictSeverity = predictSeverity;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
