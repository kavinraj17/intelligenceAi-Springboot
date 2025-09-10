package org.example.intelligenceaiapplication.controller;

import org.example.intelligenceaiapplication.service.CodeAnalysisService;
import org.example.intelligenceaiapplication.service.DataSetGenerator;
import org.example.intelligenceaiapplication.service.FeatureExtractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CodeAnalysisController {

    private final CodeAnalysisService analysisService;

    public CodeAnalysisController(CodeAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analyzeCode")
    public ResponseEntity<Map<String, Object>> analyzeCode(@RequestBody String javaCode) {
        Map<String, Object> result = analysisService.analyzeJavaCode(javaCode);
        return ResponseEntity.ok(result);
    }

    @RestController
    @RequestMapping("/api/test")
    public class FeatureTestController {

        @Autowired
        private FeatureExtractionService featureExtractionService;

        @PostMapping("/features")
        public Map<String, Integer> getFeatures(@RequestBody Map<String, String> request) {
            String javaCode = request.get("code"); // Get code from JSON request
            return featureExtractionService.extractFeatures(javaCode);
        }
    }
    @Component
    public class DatasetRunner implements CommandLineRunner {

        private final DataSetGenerator datasetGenerator;

        public DatasetRunner(DataSetGenerator datasetGenerator) {
            this.datasetGenerator = datasetGenerator;
        }

        @Override
        public void run(String... args) throws Exception {
            String folderPath = "src/main/java/org/example/intelligenceaiapplication/testjavafiles";
            String outputCsv = "code_features_dataset.csv";
            datasetGenerator.generateDataset(folderPath, outputCsv);
        }
    }
}