package com.ittalens.gag.controller;

import com.ittalens.gag.model.dto.PostDto;
import com.ittalens.gag.model.entity.Post;
import com.ittalens.gag.model.repository.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController("/post")
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping()
    public PostDto addPost(@RequestBody Post post){
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
        System.out.println("insert in DB post");
        PostDto postDto = modelMapper.map(post, PostDto.class);
        return postDto;
    }

    @GetMapping("/post/all")
    public List<PostDto> getAllPosts(){
        List<Post> posts = postRepository.findAll();
        List<PostDto> postDtos = posts.stream()
                .map(post -> modelMapper.map(post,PostDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    @GetMapping("/post/date")
    public List<PostDto> getAllPostsByDateAndTime(){
        List<Post> posts = postRepository.findAll();
        List<PostDto> postDtos = posts.stream()
                .map(post -> modelMapper.map(post,PostDto.class))
                .sorted((post1, post2) -> post1.getCreatedAt().compareTo(post2.getCreatedAt()))
                .collect(Collectors.toList());
        return postDtos;
    }

    @GetMapping("/post/word")
    public List<PostDto> getAllPostsByWord(@RequestBody String word){
        List<Post> posts = postRepository.findAll();
        List<PostDto> postDtos = posts.stream()
                .map(post -> modelMapper.map(post,PostDto.class))
                .collect(Collectors.toList());
        return postDtos;
    }
}
