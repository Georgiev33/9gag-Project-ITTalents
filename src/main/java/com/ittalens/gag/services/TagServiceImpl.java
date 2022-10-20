package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.tags.TagCreatedDTO;
import com.ittalens.gag.model.entity.TagEntity;
import com.ittalens.gag.model.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService{
    @Autowired
    public final TagRepository tagRepository;

    @Override
    public void createdTag(TagCreatedDTO tagCreatedDto) {
        TagEntity tagEntity = new TagEntity();
        tagEntity.setTagType(tagCreatedDto.getTagType());
        tagRepository.save(tagEntity);
    }
}
