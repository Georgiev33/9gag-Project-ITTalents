package com.ittalens.gag.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IFileStoreService {

    String saveFile(MultipartFile file, Long uid);

    File getFile(String pathToFile);
}
