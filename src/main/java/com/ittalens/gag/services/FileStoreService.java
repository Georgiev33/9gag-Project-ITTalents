package com.ittalens.gag.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoreService {

    String saveFile(MultipartFile file);
}
