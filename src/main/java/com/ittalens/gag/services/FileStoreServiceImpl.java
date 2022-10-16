package com.ittalens.gag.services;

import com.ittalens.gag.model.exceptions.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

@Service
@AllArgsConstructor
@Slf4j
public class FileStoreServiceImpl implements FileStoreService {

    @Override
    public String saveFile(MultipartFile file) {

        Path root = Paths.get("uploads");
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
        try {
            String newFileName = timeStamp + "-" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), root.resolve(newFileName));
            return newFileName;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException("File was not saved");
        }
    }
}
