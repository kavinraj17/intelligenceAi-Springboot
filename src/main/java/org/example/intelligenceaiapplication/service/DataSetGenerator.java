package org.example.intelligenceaiapplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Service
public class DataSetGenerator {

    @Autowired
    private FeatureExtractionService featureExtractionService;

    @Autowired
    private CodeAnalysisService codeAnalysisService;

    public void generateDataset(String folderPath, String outputCsvPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Invalid folder path: " + folderPath);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputCsvPath))) {
            // Write CSV header
            writer.write("numMethods,numConstructors,numVariables,numEmptyMethods,numEmptyCatchBlocks,classLines,severity");
            writer.newLine();

            File[] files = folder.listFiles((dir, name) -> name.endsWith(".java"));
            if (files == null) return;

            for (File file : files) {
                String javaCode = Files.readString(Paths.get(file.getAbsolutePath()));

                // Extract features
                Map<String, Integer> features = featureExtractionService.extractFeatures(javaCode);

                // Get severity from rule-based analyzer
                Map<String, Object> analysis = codeAnalysisService.analyzeJavaCode(javaCode);
                String severity = (String) analysis.get("severity");

                // Write row to CSV
                writer.write(
                        features.getOrDefault("numMethods",0) + "," +
                                features.getOrDefault("numConstructors",0) + "," +
                                features.getOrDefault("numVariables",0) + "," +
                                features.getOrDefault("numEmptyMethods",0) + "," +
                                features.getOrDefault("numEmptyCatchBlocks",0) + "," +
                                features.getOrDefault("classLines",0) + "," +
                                severity
                );
                writer.newLine();
            }

            System.out.println("Dataset generated successfully: " + outputCsvPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
