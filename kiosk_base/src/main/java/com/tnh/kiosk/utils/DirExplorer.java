package com.tnh.kiosk.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.File;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DirExplorer {

    Filter filter;
    FileHandler fileHandler;

    public void explore(File root) {
        explore(0, "", root);
    }

    private void explore(int level, String path, File file) {
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                explore(level + 1, path + "/" + child.getName(), child);
            }
        } else if (filter.interested(level, path, file)) {
            fileHandler.handle(level, path, file);
        }
    }

    @FunctionalInterface
    public interface FileHandler {
        void handle(int level, String path, File file);
    }

    @FunctionalInterface
    public interface Filter {
        boolean interested(int level, String path, File file);
    }
}