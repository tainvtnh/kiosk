package com.tnh.kiosk.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
public class MessagesExtractor {

    public static void main(String[] args) throws IOException {
        Set<String> newMessages = new TreeSet<>();
        Properties existingMessages = new Properties();
        String propertiesFilePath = "src/main/resources/i18n/messages_vi.properties";

        File propertiesFile = new File(propertiesFilePath);
        if (propertiesFile.exists()) {
            try (var fis = new FileInputStream(propertiesFile)) {
                existingMessages.load(fis);
            }
        }

        File projectDir = new File("src/main/java");

        new DirExplorer(
                (level, path, file) -> path.endsWith(".java"),
                (level, path, file) -> {
                    log.info(LogStyleHelper.info("Parsing file: " + file.getAbsolutePath()));
                    try {
                        JavaParser parser = new JavaParser();
                        var result = parser.parse(file);
                        if (result.isSuccessful() && result.getResult().isPresent()) {
                            CompilationUnit cu = result.getResult().get();
                            cu.accept(new MethodCallVisitor(), newMessages);
                            cu.accept(new AnnotationVisitor(), newMessages);
                        } else {
                            log.warn(LogStyleHelper.warn("File not parsed: " + file.getAbsolutePath()));
                        }
                    } catch (Exception e) {
                        log.error(LogStyleHelper.error("Error parsing file: " + file.getAbsolutePath()), e.getMessage());
                    }
                }
        ).explore(projectDir);

        Set<String> allKeys = new TreeSet<>(existingMessages.stringPropertyNames());
        allKeys.addAll(newMessages);

        try (var writer = new FileWriter(propertiesFile)) {
            for (String key : allKeys) {
                String value = existingMessages.getProperty(key, "");
                writer.write(key + "=" + value + "\n");
            }
        }

        log.info(LogStyleHelper.info("Messages extracted and saved to " + propertiesFilePath));
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<Set<String>> {
        @Override
        public void visit(MethodCallExpr methodCall, Set<String> messages) {
            super.visit(methodCall, messages);

            if (methodCall.getNameAsString().equals("getMessage")) {
                methodCall.getArguments().stream()
                        .filter(Expression::isStringLiteralExpr)
                        .map(arg -> arg.asStringLiteralExpr().getValue())
                        .forEach(messages::add);
            }
        }
    }

    private static class AnnotationVisitor extends VoidVisitorAdapter<Set<String>> {
        @Override
        public void visit(com.github.javaparser.ast.body.FieldDeclaration field, Set<String> messages) {
            super.visit(field, messages);
            field.getAnnotations().forEach(annotation -> annotation.getChildNodes().forEach(child -> {
                String text = child.toString();
                if (text.contains("message") && text.contains("{") && text.contains("}")) {
                    String key = text.replaceAll(".*message\\s*=\\s*\"\\{([^}]+)}\".*", "$1");
                    if (!key.isBlank()) {
                        messages.add(key);
                    }
                }
            }));
        }
    }
}