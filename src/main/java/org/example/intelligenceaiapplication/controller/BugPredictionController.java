package org.example.intelligenceaiapplication.controller;

import org.example.intelligenceaiapplication.dto.BugPredictionResponse;
import org.example.intelligenceaiapplication.service.BugPredictionService;
import org.springframework.web.bind.annotation.*;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class BugPredictionController{
    private final BugPredictionService bugPredictionService;

    public BugPredictionController(BugPredictionService bugPredictionService){
        this.bugPredictionService = bugPredictionService;
    }

    //SINGLE PREDICTION
    @GetMapping("/predictBug")
    public  BugPredictionResponse predictBug(@RequestParam String description)
    {
        String severity = bugPredictionService.predictSeverity(description);
        return new BugPredictionResponse(description,severity);
    }

    //BATCH PREDICTION
    @PostMapping("/predictBugs")
    public List<BugPredictionResponse> predictBugs(@RequestBody List<String> descriptions){
        List<BugPredictionResponse> results = new ArrayList<>();

        for(String desc:descriptions){
            String severity =bugPredictionService.predictSeverity(desc);
            results.add(new BugPredictionResponse(desc,severity));
        }
        return results;
    }
}