package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.PostCreateReqDto;
import com.ittalens.gag.model.dto.PostRespDto;
import com.ittalens.gag.model.entity.Post;
import com.ittalens.gag.model.exceptions.NotFoundException;
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
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setResourcePath(internalFileName);
        post.setCreatedAt(LocalDateTime.now());
        post.setCreatedBy(1);
        post.setCategoryId(postDto.getCategoryId());
        postRepository.save(post);

    }

    public List<PostRespDto> getAllByCreationDate() {
        List<Post> posts = postRepository.findByOrderByCreatedAtAsc();
        List<PostRespDto> postDtos = posts.stream()
                .map(post -> modelMapper.map(post, PostRespDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public List<PostRespDto> getAllPostsDto() {
        List<Post> posts = postRepository.findAll();
        List<PostRespDto> postDtos = posts.stream()
                .map(post -> modelMapper.map(post, PostRespDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public List<PostRespDto> findPostsByWord(String word) {
        List<Post> posts = postRepository.getByTitle(word);
        if (posts.isEmpty()) {
            throw new NotFoundException("Not found post with this title");
        }
        List<PostRespDto> postDtos = posts.stream()
                .map(post -> modelMapper.map(post, PostRespDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }
}
