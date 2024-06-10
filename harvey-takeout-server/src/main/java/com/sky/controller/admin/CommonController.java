package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("upload file, param is {}", file);

        try {
            String originalFileName = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + originalFileName.substring(originalFileName.lastIndexOf("."));
            String filepath = aliOssUtil.upload(file.getBytes(), fileName);
            return Result.success(filepath);
        } catch (Exception e) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
