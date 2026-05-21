package com.navatation.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @Author admin
 * @CreateTime 2026-05-21
 * @Description 通用文件上传处理工具类，提供唯一文件名生成、带重试限制的目录创建及物理保存功能
 */
public class FileUploadUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUploadUtil.class);

    /** 默认创建目录的最大重试次数 */
    private static final int MAX_RETRY_COUNT = 3;

    /** 重试间隔时间（毫秒） */
    private static final long RETRY_INTERVAL_MS = 50;

    /**
     * 生成绝对不重复的长字符串文件名（保留原文件名扩展名）
     * @param originalFilename 原始文件名
     * @return 唯一的长字符串文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String ext = "";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }

    /**
     * 安全地创建目录（带有失败次数限制与重试机制）
     * @param dirPath 目标目录的绝对路径
     * @return 目录的 Path 对象
     */
    public static Path createDirectoryWithRetry(String dirPath) {
        if (!StringUtils.hasText(dirPath)) {
            throw new IllegalArgumentException("目录路径不能为空");
        }

        Path path = Paths.get(dirPath);
        if (Files.exists(path)) {
            return path;
        }

        int attempts = 0;
        while (attempts < MAX_RETRY_COUNT) {
            attempts++;
            try {
                Files.createDirectories(path);
                log.info("目录创建成功: {}, 尝试次数: {}", dirPath, attempts);
                return path;
            } catch (IOException e) {
                log.warn("尝试创建目录失败: {}, 当前尝试次数: {}, 错误原因: {}", dirPath, attempts, e.getMessage());
                if (attempts >= MAX_RETRY_COUNT) {
                    log.error("创建目录达到最大限制尝试次数: {}, 依旧失败", dirPath);
                    throw new RuntimeException("创建上传目录失败，请联系管理员", e);
                }
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("创建目录线程被中断", ie);
                }
            }
        }
        throw new RuntimeException("创建目录失败：" + dirPath);
    }

    /**
     * 保存上传的文件
     * @param file Spring MultipartFile 对象
     * @param targetDir 目标绝对路径目录
     * @return 保存成功后的唯一长字符串文件名
     */
    public static String saveFile(MultipartFile file, String targetDir) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (!StringUtils.hasText(targetDir)) {
            throw new IllegalArgumentException("目标保存目录不能为空");
        }

        // 1. 带重试限制地安全创建目录
        createDirectoryWithRetry(targetDir);

        // 2. 生成不重复的唯一文件名
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        Path targetPath = Paths.get(targetDir, uniqueFileName);

        // 3. 物理保存文件
        try {
            file.transferTo(targetPath.toAbsolutePath().toFile());
            log.info("文件物理保存成功: {}", targetPath.toAbsolutePath());
            return uniqueFileName;
        } catch (IOException e) {
            log.error("文件物理保存发生IO异常: {}", targetPath.toAbsolutePath(), e);
            throw new RuntimeException("文件物理保存失败，系统异常", e);
        }
    }
}
