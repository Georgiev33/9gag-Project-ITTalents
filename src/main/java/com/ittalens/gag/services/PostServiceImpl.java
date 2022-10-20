package com.ittalens.gag.services;

import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import com.ittalens.gag.model.dto.tags.TagCreatedDTO;
import com.ittalens.gag.model.entity.PostEntity;
import com.ittalens.gag.model.entity.TagEntity;
import com.ittalens.gag.model.entity.User;
import com.ittalens.gag.model.entity.UserPostReaction;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.repository.PostReactionsRepository;
import com.ittalens.gag.model.repository.PostRepository;
import com.ittalens.gag.model.repository.TagRepository;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    PostReactionsRepository reactionsRepository;


    @Override
    public void createPost(PostCreateReqDTO postDto) {
        MultipartFile originalFile = postDto.getFile();
        String internalFileName = fileStoreService.saveFile(originalFile);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDto.getTitle());
        postEntity.setResourcePath(internalFileName);
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setCreatedBy(userSessionService.currentUserId());
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
                TagCreatedDTO tagCreatedDto = new TagCreatedDTO();
                tagCreatedDto.setTagType(type);
                tagService.createdTag(tagCreatedDto);
            }
            tagEntityList.add(tagEntity);
        }

        return tagEntityList;
    }

    public List<PostRespDTO> getAllByCreationDate() {
        List<PostEntity> postEntities = postRepository.findByOrderByCreatedAtDesc();
        List<PostRespDTO> postDtos = postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDTO.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public List<PostRespDTO> getAllPostsDto() {
        List<PostEntity> postEntities = postRepository.findAll();
        List<PostRespDTO> postDtos = postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDTO.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public List<PostRespDTO> findPostsByWord(String word) {
        List<PostEntity> postEntities = postRepository.findByTitleContains(word);

        List<PostRespDTO> postDtos = postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDTO.class))
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
    public PostReactionResponseDTO react(long pid, boolean status){
        Long userId = userSessionService.currentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("No such user."));
        PostEntity post = postRepository.findById(pid).orElseThrow(() -> new NotFoundException("No such post."));
        UserPostReaction.PostReactionKey key = new UserPostReaction.PostReactionKey();
        key.setPostId(pid);
        key.setUserId(userId);

        UserPostReaction reaction = new UserPostReaction();
        reaction.setPost(post);
        reaction.setUser(user);
        reaction.setStatus(status);
        reaction.setId(key);
        reactionsRepository.save(reaction);

        PostReactionResponseDTO responseDTO = new PostReactionResponseDTO();
        responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndIdIs(key));
        responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndIdIs(key));
        responseDTO.setCurrentReactionStatus(status);
        return responseDTO;
    }

}
