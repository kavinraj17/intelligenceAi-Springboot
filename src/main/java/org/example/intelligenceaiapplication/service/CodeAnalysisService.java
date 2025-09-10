package org.example.intelligenceaiapplication.service;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;


@Service
    public class CodeAnalysisService{
        public Map<String,Object> analyzeJavaCode(String Javacode){
            Map<String,Object> response = new HashMap<>();
            List<String> issues = new ArrayList<>();
            AtomicReference<String> severity = new AtomicReference<>("Low");

            try{
                JavaParser parser = new JavaParser();
                CompilationUnit cu = parser.parse(Javacode)
                        .getResult()
                        .orElseThrow(() -> new RuntimeException("Failed to parse code"));

                //Empty Methods
                cu.findAll(MethodDeclaration.class).forEach(method ->{
                    if(!method.getBody().isPresent()||method.getBody().get().isEmpty()){
                        issues.add("Empty Method: "+ method.getName());
                    }
                });

                cu.findAll(VariableDeclarator.class).forEach(var->{
                    String varName = var.getNameAsString();
                    long usageCount = cu.findAll(NameExpr.class,nameExpr -> nameExpr.getNameAsString().equals(varName)).size();
                    if(usageCount<=1)
                    {
                        issues.add("Unused Variable :"+varName);
                    }
                });

                cu.findAll(VariableDeclarator.class).forEach(var -> {
                    if (var.getType().isClassOrInterfaceType()) {
                        issues.add("Check for potential null: " + var.getName());
                        severity.set("Medium");
                    }
                });
            }catch(Exception e){
                issues.add("Error parsing code: " + e.getMessage());
                severity.set("HIGH");
            }
            response.put("severity", severity.get());
            response.put("issues", issues);

            return response;
        }
    }


