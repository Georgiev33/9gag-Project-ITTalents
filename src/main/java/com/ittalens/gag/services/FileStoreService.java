package com.ittalens.gag.services;

import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FileStoreService {

    private static final List<String> AVAILABLE_FILE_TYPE = Arrays.asList("jpeg", "png", "mp4", "m4v");

    public String saveFile(MultipartFile file) {
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());

        if (!AVAILABLE_FILE_TYPE.contains(ext)){
            throw new UnauthorizedException("Invalid file type");
        }
        Path pathToFile = Paths.get("uploads");

        try {
            String newFileName = System.nanoTime() + "." + ext;
            Files.copy(file.getInputStream(), pathToFile.resolve(newFileName));
            return newFileName;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException("File was not saved");
        }
    }

    public File getFile(String pathToFile){
        File file =  new File("uploads" +File.separator + pathToFile);
        if (!file.exists()){
            throw new NotFoundException("File does not exist!");
        }
        return file;
    }
}
