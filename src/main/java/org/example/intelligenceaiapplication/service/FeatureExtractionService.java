package org.example.intelligenceaiapplication.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.CatchClause;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeatureExtractionService {

    public Map<String, Integer> extractFeatures(String javaCode) {
        Map<String, Integer> features = new HashMap<>();
        try {
            CompilationUnit cu = new JavaParser().parse(javaCode)
                    .getResult().orElseThrow(() -> new RuntimeException("Parse failed"));

            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                features.put("numMethods", clazz.getMethods().size());
                features.put("numConstructors", clazz.getConstructors().size());
                features.put("numVariables", clazz.findAll(VariableDeclarator.class).size());

                long emptyMethods = clazz.getMethods().stream()
                        .filter(m -> !m.getBody().isPresent() || m.getBody().get().isEmpty())
                        .count();
                features.put("numEmptyMethods", (int) emptyMethods);

                long emptyCatch = cu.findAll(CatchClause.class).stream()
                        .filter(c -> c.getBody().isEmpty())
                        .count();
                features.put("numEmptyCatchBlocks", (int) emptyCatch);

                int classLines = clazz.getEnd().map(pos -> pos.line).orElse(0)
                        - clazz.getBegin().map(pos -> pos.line).orElse(0);
                features.put("classLines", classLines);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return features;
    }
}
