package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.PostCreateReqDto;
import com.ittalens.gag.model.dto.PostRespDto;
import com.ittalens.gag.model.entity.PostEntity;
import com.ittalens.gag.model.repository.PostRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final FileStoreService fileStoreService;
    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public void createPost(PostCreateReqDto postDto) {

        MultipartFile originalFile = postDto.getFile();

        String internalFileName = fileStoreService.saveFile(originalFile);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDto.getTitle());
        postEntity.setResourcePath(internalFileName);
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setCreatedBy(1);
        postEntity.setCategoryId(postDto.getCategoryId());
        postRepository.save(postEntity);

    }

    public List<PostRespDto> getAllByCreationDate() {
        List<PostEntity> postEntities = postRepository.findByOrderByCreatedAtAsc();
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
}
