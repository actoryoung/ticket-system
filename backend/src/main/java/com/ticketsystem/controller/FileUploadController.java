package com.ticketsystem.controller;

import com.ticketsystem.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 1. 创建上传目录
            String uploadPath = System.getProperty("user.dir") + File.separator + uploadDir;
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // 3. 保存文件
            Path filePath = Paths.get(uploadPath, filename);
            Files.write(filePath, file.getBytes());

            // 4. 返回文件访问URL
            String fileUrl = "/uploads/" + filename;
            log.info("文件上传成功, filename={}, url={}", originalFilename, fileUrl);

            return Result.success("文件上传成功", fileUrl);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传多个文件
     */
    @PostMapping("/upload/multiple")
    public Result<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.error("文件不能为空");
        }

        List<String> fileUrls = new ArrayList<>();

        try {
            // 1. 创建上传目录
            String uploadPath = System.getProperty("user.dir") + File.separator + uploadDir;
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 2. 保存所有文件
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFilename = file.getOriginalFilename();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String filename = UUID.randomUUID().toString() + extension;

                    Path filePath = Paths.get(uploadPath, filename);
                    Files.write(filePath, file.getBytes());

                    String fileUrl = "/uploads/" + filename;
                    fileUrls.add(fileUrl);

                    log.info("文件上传成功, filename={}, url={}", originalFilename, fileUrl);
                }
            }

            return Result.success("文件上传成功", fileUrls);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
