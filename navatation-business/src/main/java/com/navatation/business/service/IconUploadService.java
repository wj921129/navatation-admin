package com.navatation.business.service;

import com.navatation.business.dto.resp.nav.IconUploadRespDTO;
import com.navatation.common.BizException;
import com.navatation.common.NavConstants;
import com.navatation.common.RedisConstants;
import com.navatation.common.ResultCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;
import com.navatation.common.FileUploadUtil;

/**
 * IconUploadService 功能描述
 *
 * @date 2026-06-09
 */
@Service
@RequiredArgsConstructor
public class IconUploadService {

    private static final Logger log = LoggerFactory.getLogger(IconUploadService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.upload.icon-path}")
    private String iconPath;

        /**
     * uploadIcon 方法
     */
    public IconUploadRespDTO uploadIcon(String userId, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !NavConstants.ALLOWED_MIME_TYPES.contains(contentType)) {
            log.warn("图标上传类型不合法 userId={}, contentType={}", userId, contentType);
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "不支持的图片格式，仅允许 PNG/JPEG/GIF/WebP/ICO/SVG");
        }

        if (file.getSize() > NavConstants.MAX_ICON_SIZE) {
            log.warn("图标上传超出大小限制 userId={}, size={}", userId, file.getSize());
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "图标文件不能超过 200KB");
        }

        String rateKey = RedisConstants.KEY_NAV_RATE_UPLOAD + userId;
        Long count = redisTemplate.opsForValue().increment(rateKey);
        if (count != null && count == 1) {
            redisTemplate.expire(rateKey, 1, TimeUnit.HOURS);
        }
        if (count != null && count > NavConstants.MAX_UPLOADS_PER_HOUR) {
            log.warn("图标上传频率超限 userId={}, count={}", userId, count);
            throw new BizException(ResultCode.TOO_MANY_REQUESTS);
        }

        try {
            String targetDir = iconPath + java.io.File.separator + userId;
            String uniqueFileName = FileUploadUtil.saveFile(file, targetDir);
            log.info("图标上传成功 userId={}, filename={}", userId, uniqueFileName);

            return new IconUploadRespDTO("/uploads/icon/custom/" + userId + "/" + uniqueFileName);
        } catch (Exception e) {
            log.error("图标文件保存失败 userId={}", userId, e);
            throw new BizException(ResultCode.INTERNAL_ERROR);
        }
    }
}
