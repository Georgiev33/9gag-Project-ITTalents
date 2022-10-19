package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.posts.PostCreateReqDto;
import com.ittalens.gag.model.dto.posts.PostRespDto;
import com.ittalens.gag.model.dto.tags.TagCreatedDto;
import com.ittalens.gag.model.entity.PostEntity;
import com.ittalens.gag.model.entity.TagEntity;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.repository.PostRepository;
import com.ittalens.gag.model.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final TagRepository tagRepository;
    @Autowired
    private final FileStoreService fileStoreService;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final TagService tagService;
    @Autowired
    private final UserSessionServiceImpl userSessionService;


    @Override
    public void createPost(PostCreateReqDto postDto) {

        MultipartFile originalFile = postDto.getFile();

        String internalFileName = fileStoreService.saveFile(originalFile);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDto.getTitle());
        postEntity.setResourcePath(internalFileName);
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setCreatedBy(postDto.getUserId());
        postEntity.setCategoryId(postDto.getCategoryId());
        postEntity.getTags().addAll(setTagsFromPostDto(postDto.getTagTypes()));
        postRepository.save(postEntity);

    }

    private List<TagEntity> setTagsFromPostDto(List<String> tags) {
        List<TagEntity> tagEntityList = new ArrayList<>();

        for (String type : tags) {
            TagEntity tagEntity = new TagEntity();
            tagEntity = tagRepository.findByTagType(type);

            if (tagEntity == null) {
                TagCreatedDto tagCreatedDto = new TagCreatedDto();
                tagCreatedDto.setTagType(type);
                tagService.createdTag(tagCreatedDto);
            }
            tagEntityList.add(tagEntity);
        }

        return tagEntityList;
    }

    public List<PostRespDto> getAllByCreationDate() {
        List<PostEntity> postEntities = postRepository.findByOrderByCreatedAtDesc();
        List<PostRespDto> postDtos = postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public List<PostRespDto> getAllPostsDto() {
        List<PostEntity> postEntities = postRepository.findAll();
        List<PostRespDto> postDtos = postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public List<PostRespDto> findPostsByWord(String word) {
        List<PostEntity> postEntities = postRepository.findByTitleContains(word);

        List<PostRespDto> postDtos = postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public void deletedPostById(Long id) {
        PostEntity post = postRepository.findById(id).orElse(null);
        if (post == null) {
            throw new NotFoundException("This post does not exist");
        }
        postRepository.delete(post);
    }
}
