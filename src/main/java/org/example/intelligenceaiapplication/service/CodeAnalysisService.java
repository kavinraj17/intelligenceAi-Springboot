package org.example.intelligenceaiapplication.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CodeAnalysisService {

    private static final int MAX_METHODS = 10; // Threshold for too many methods
    private static final int MAX_CLASS_LINES = 100; // Threshold for class too large

    public Map<String, Object> analyzeJavaCode(String javaCode) {
        Map<String, Object> response = new HashMap<>();
        List<String> issues = new ArrayList<>();
        AtomicReference<String> severity = new AtomicReference<>("Low");

        try {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(javaCode)
                    .getResult()
                    .orElseThrow(() -> new RuntimeException("Failed to parse code"));

            // Analyze each class
            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                String className = clazz.getNameAsString();

                // 1️⃣ Missing constructor
                if (clazz.getConstructors().isEmpty()) {
                    issues.add("Missing constructor in class: " + className);
                    severity.set("Medium");
                }

                // 2️⃣ Too many methods
                List<MethodDeclaration> methods = clazz.getMethods();
                if (methods.size() > MAX_METHODS) {
                    issues.add("Too many methods in class: " + className + " (" + methods.size() + ")");
                    severity.set("Medium");
                }

                // 3️⃣ Class too large
                int classLines = clazz.getEnd().map(pos -> pos.line).orElse(0)
                        - clazz.getBegin().map(pos -> pos.line).orElse(0);
                if (classLines > MAX_CLASS_LINES) {
                    issues.add("Class too large: " + className + " (" + classLines + " lines)");
                    severity.set("Medium");
                }

                // 4️⃣ Empty catch blocks
                cu.findAll(CatchClause.class).forEach(catchClause -> {
                    if (catchClause.getBody().isEmpty()) {
                        issues.add("Empty catch block in class: " + className);
                        severity.set("Medium");
                    }
                });

                // 5️⃣ Empty methods
                methods.forEach(method -> {
                    if (!method.getBody().isPresent() || method.getBody().get().isEmpty()) {
                        issues.add("Empty Method: " + method.getName() + " in class: " + className);
                        severity.set("Low");
                    }
                });

                // 6️⃣ Unused variables
                clazz.findAll(VariableDeclarator.class).forEach(var -> {
                    String varName = var.getNameAsString();
                    long usageCount = cu.findAll(NameExpr.class, nameExpr -> nameExpr.getNameAsString().equals(varName)).size();
                    if (usageCount <= 1) { // declared but not used
                        issues.add("Unused Variable: " + varName + " in class: " + className);
                        severity.set("Low");
                    }
                });

                // 7️⃣ Potential null (class type variables)
                clazz.findAll(VariableDeclarator.class).forEach(var -> {
                    if (var.getType().isClassOrInterfaceType()) {
                        issues.add("Check for potential null: " + var.getName() + " in class: " + className);
                        severity.set("Medium");
                    }
                });
            });

        } catch (Exception e) {
            issues.add("Error parsing code: " + e.getMessage());
            severity.set("High");
        }

        response.put("severity", severity.get());
        response.put("issues", issues);
        return response;
    }
}
