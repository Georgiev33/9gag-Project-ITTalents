package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.tags.TagCreatedDto;
import com.ittalens.gag.services.TagServiceImpl;
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
    public final TagServiceImpl tagService;

    @PostMapping("/tag")
    public ResponseEntity<?> createdTag(@RequestBody TagCreatedDto tagCreatedDto){
        tagService.createdTag(tagCreatedDto);
        return ResponseEntity.ok().build();
    }
}
