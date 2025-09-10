package org.example.intelligenceaiapplication.controller;

import org.example.intelligenceaiapplication.service.DataSetGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSetRunner implements CommandLineRunner {

    private DataSetGenerator datasetGenerator;

    public void DatasetRunner(DataSetGenerator datasetGenerator) {
        this.datasetGenerator = datasetGenerator;
    }

    public DataSetRunner(DataSetGenerator datasetGenerator) {
        this.datasetGenerator = datasetGenerator;
    }

    @Override
    public void run(String... args) throws Exception {
        String folderPath = "src/main/java/org/example/intelligenceaiapplication/testjavafiles";
        String outputCsv = "code_features_dataset.csv";
        datasetGenerator.generateDataset(folderPath, outputCsv);
    }
}