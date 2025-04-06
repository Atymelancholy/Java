package com.example.bookblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log Controller", description = "Асинхронное создание и получение лог-файлов")
public class Log {

    private static final String LOG_FILE_PATH = "logs/application.log";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
    private static final Map<String, String> taskStatusMap = new ConcurrentHashMap<>();
    private static final Map<String, String> taskFileMap = new ConcurrentHashMap<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Operation(summary = "Создать лог-файл", description = "Асинхронное создание лог-файла")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Задача на создание принята"),
    })
    @PostMapping("/create")

    public ResponseEntity<Map<String, String>> createLogFile() {
        String taskId = UUID.randomUUID().toString();
        taskStatusMap.put(taskId, "PENDING");

        executor.submit(() -> {
                Thread.sleep(10000);
                taskStatusMap.put(taskId, "PROCESSING");

                Path sourcePath = Paths.get(LOG_FILE_PATH);

                if (!sourcePath.toFile().exists()) {
                    taskStatusMap.put(taskId, "ERROR: Source log file not found");
                    return;
                }

                String tempFileName = "log_" + taskId + ".log";
                Path tempFilePath = Paths.get(TEMP_DIR, tempFileName);

                Files.copy(sourcePath, tempFilePath);

                taskFileMap.put(taskId, tempFilePath.toString());
                taskStatusMap.put(taskId, "COMPLETED");
        });

        return ResponseEntity.accepted().body(Map.of(
                "task_id", taskId,
                "status", "PENDING"
        ));
    }


    @Operation(summary = "Проверить статус", description = "Проверить статус создания лог-файла")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус задачи"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/status/{taskId}")
    public ResponseEntity<Map<String, String>> getTaskStatus(@PathVariable String taskId) {
        String status = taskStatusMap.get(taskId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "task_id", taskId,
                "status", status
        ));
    }

    @Operation(summary = "Скачать лог-файл", description = "Получить созданный лог-файл")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Файл доступен"),
        @ApiResponse(responseCode = "404", description = "Файл не найден или не готов")
    })
    @GetMapping("/download/{taskId}")
    public ResponseEntity<Resource> downloadLogFile(@PathVariable String taskId)
            throws IOException {
        String status = taskStatusMap.get(taskId);
        if (status == null || !"COMPLETED".equals(status)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        String filePath = taskFileMap.get(taskId);
        if (filePath == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(filePath);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
