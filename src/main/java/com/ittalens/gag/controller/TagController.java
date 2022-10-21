package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.tags.TagCreatedDTO;
import com.ittalens.gag.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TagController {

    @Autowired
    public final TagService tagService;

    @PostMapping("/tag")
    public ResponseEntity<?> createdTag(@RequestBody TagCreatedDTO tagCreatedDto){
        tagService.createdTag(tagCreatedDto);
        return ResponseEntity.ok().build();
    }
}
