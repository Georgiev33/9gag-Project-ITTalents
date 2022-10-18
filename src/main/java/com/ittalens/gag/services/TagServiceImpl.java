package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.tags.TagCreatedDto;
import com.ittalens.gag.model.entity.TagEntity;
import com.ittalens.gag.model.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService{
    @Autowired
    public final TagRepository tagRepository;

    @Override
    public void createdTag(TagCreatedDto tagCreatedDto) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setTagType(tagCreatedDto.getTagType());
        tagRepository.save(tagEntity);
    }
}
