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
import org.aspectj.weaver.ast.Not;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

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
    private final UserRepository userRepository;
    @Autowired
    private final PostReactionsRepository reactionsRepository;


    public void createPost(PostCreateReqDTO postDto,Long userId) {
        MultipartFile originalFile = postDto.getFile();
        String internalFileName = fileStoreService.saveFile(originalFile);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDto.getTitle());
        postEntity.setResourcePath(internalFileName);
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setCreatedBy(userId);
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
                tagEntity = tagRepository.findByTagType(type);
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

        if (postEntities.isEmpty()){
            throw new NotFoundException("Don't have a post with this word");
        }

        List<PostRespDTO> postDtos = postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDTO.class))
                .collect(Collectors.toList());
        return postDtos;
    }

    public void deletedPostById(Long id) {
        PostEntity post = postRepository.findById(id).orElseThrow(() ->  new NotFoundException("This post does not exist"));
        postRepository.delete(post);
    }

    public PostReactionResponseDTO react(Long pId, Long uId, boolean status){
        User user = userRepository.findById(uId).orElseThrow(() -> new NotFoundException("No such user."));
        PostEntity post = postRepository.findById(pId).orElseThrow(() -> new NotFoundException("No such post."));

        UserPostReaction.PostReactionKey key = new UserPostReaction.PostReactionKey();
        key.setPostId(pId);
        key.setUserId(uId);
        if(reactionsRepository.findById(key).isPresent() && reactionsRepository.findById(key).get().isStatus() == status){
            return deleteReaction(key);
        }

        UserPostReaction reaction = new UserPostReaction();
        reaction.setPost(post);
        reaction.setUser(user);
        reaction.setStatus(status);
        reaction.setId(key);
        reactionsRepository.save(reaction);

        PostReactionResponseDTO responseDTO = new PostReactionResponseDTO();
        responseDTO.setId(pId);
        responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndIdIs(key));
        responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndIdIs(key));
        responseDTO.setCurrentReactionStatus(status);
        return responseDTO;
    }

    private PostReactionResponseDTO deleteReaction(UserPostReaction.PostReactionKey key){
        UserPostReaction reaction = reactionsRepository.findById(key).orElseThrow(() -> new NotFoundException("Reaction not found."));
        PostReactionResponseDTO responseDTO = new PostReactionResponseDTO();
        responseDTO.setId(reaction.getPost().getId());
        if(reaction.isStatus()) {
            responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndIdIs(key) - 1);
            responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndIdIs(key));
        }else{
            responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndIdIs(key));
            responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndIdIs(key) - 1);
        }
        reactionsRepository.delete(reaction);
        return responseDTO;
    }

    public PostRespDTO getPostById(String pid) {
        Long postId = Long.parseLong(pid);
        PostEntity post = postRepository.findById(postId).orElseThrow(() ->  new NotFoundException("This post does not exist"));
        PostRespDTO postRespDTO = modelMapper.map(post, PostRespDTO.class);
        return postRespDTO;
    }

    public File takeFile(String pid) {
        PostRespDTO postRespDTO = getPostById(pid);
        return fileStoreService.getFile(postRespDTO.getResourcePath());
    }
}
