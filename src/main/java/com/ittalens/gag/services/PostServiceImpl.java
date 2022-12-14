package com.ittalens.gag.services;

import com.ittalens.gag.model.dao.PostDAO;
import com.ittalens.gag.model.dto.posts.PostCreateReqDTO;
import com.ittalens.gag.model.dto.posts.PostReactionResponseDTO;
import com.ittalens.gag.model.dto.posts.PostRespDTO;
import com.ittalens.gag.model.dto.tags.TagCreatedDTO;
import com.ittalens.gag.model.entity.PostEntity;
import com.ittalens.gag.model.entity.TagEntity;
import com.ittalens.gag.model.entity.User;
import com.ittalens.gag.model.entity.UserPostReaction;
import com.ittalens.gag.model.exceptions.BadRequestException;
import com.ittalens.gag.model.exceptions.NotFoundException;
import com.ittalens.gag.model.repository.PostReactionsRepository;
import com.ittalens.gag.model.repository.PostRepository;
import com.ittalens.gag.model.repository.TagRepository;
import com.ittalens.gag.model.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostServiceImpl implements IPostService {

    @Autowired
    private final PostRepository postRepository;
    @Autowired
    private final TagRepository tagRepository;
    @Autowired
    private final FileStoreServiceImpl fileStoreServiceImpl;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final TagServiceImpl tagServiceImpl;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PostReactionsRepository reactionsRepository;
    @Autowired
    private final PostDAO dao;
    @Autowired
    private final ConfigPropertiesService configPropertiesService;
    @Autowired
    private final EmailSenderServiceImpl emailSenderService;
    @Autowired
    private final UserServiceImpl userService;

    @Override
    public PostRespDTO createPost(PostCreateReqDTO postDto, Long userId) {

        if (postDto.getTitle() == null || postDto.getCategoryId() == null || postDto.getFile() == null || postDto.getFile().isEmpty()) {
            throw new BadRequestException("Some fields are missing!");
        }

        MultipartFile originalFile = postDto.getFile();
        String internalFileName = fileStoreServiceImpl.saveFile(originalFile, userId);
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDto.getTitle());
        postEntity.setResourcePath(internalFileName);
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setCreatedBy(userId);
        postEntity.setCategoryId(postDto.getCategoryId());
        postEntity.getTags().addAll(setTagsFromPostDto(postDto.getTags()));
        postRepository.save(postEntity);

        PostRespDTO postRespDTO = modelMapper.map(postEntity, PostRespDTO.class);
        postRespDTO.setResourceURL("http://localhost:" + configPropertiesService.getServerPort() + "/posts/download/" + postRespDTO.getId());
        emailSenderService.sendSuccessfulUploadPost(userService.getUserById(userId));
        return postRespDTO;
    }

    @Override
    public Page<PostRespDTO> getAllByCreationDate(int offset, int pageSize, String sortType) {
        validatePage(offset);
        Page<PostEntity> postEntities = null;
        if (sortType.toLowerCase().equals("desc")) {
            postEntities = postRepository.findByOrderByCreatedAtDesc(PageRequest.of((offset - 1), pageSize));
        } else if (sortType.toLowerCase().equals("asc")) {
            postEntities = postRepository.findByOrderByCreatedAtAsc(PageRequest.of((offset - 1), pageSize));
        } else {
            throw new BadRequestException("No such filtering option.");
        }
        Page<PostRespDTO> postDtos = pageMappingToDTO(postEntities);
        setURL(postDtos);
        return postDtos;

    }

    @Override
    public Page<PostRespDTO> findPostsByWord(String word, int offset, int pageSize) {
        validatePage(offset);
        Page<PostEntity> postEntities = postRepository.findByTitleContains(word, PageRequest.of((offset - 1), pageSize));
        if (postEntities.isEmpty()) {
            throw new NotFoundException("Don't have a post with this word");
        }

        Page<PostRespDTO> postDtos = pageMappingToDTO(postEntities);
        setURL(postDtos);
        return postDtos;
    }

    @Override
    public void deletedPostById(Long id, Long userId) {
        PostEntity post = postRepository.findById(id).orElseThrow(() -> new NotFoundException("This post does not exist"));
        if (!post.getCreatedBy().equals(userId)) {
            throw new BadRequestException("Can't delete a post that is not your own.");
        }
        postRepository.delete(post);
    }

    @Override
    public PostReactionResponseDTO react(Long pId, Long uId, boolean status) {
        User user = userRepository.findById(uId).orElseThrow(() -> new NotFoundException("No such user."));
        PostEntity post = postRepository.findById(pId).orElseThrow(() -> new NotFoundException("No such post."));

        UserPostReaction.PostReactionKey key = new UserPostReaction.PostReactionKey();
        key.setPostId(pId);
        key.setUserId(uId);
        if (reactionsRepository.findById(key).isPresent() && reactionsRepository.findById(key).get().isStatus() == status) {
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
        responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndPostId(post.getId()));
        responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndPostId(post.getId()));
        responseDTO.setCurrentReactionStatus(status);
        return responseDTO;
    }

    @Override
    public PostRespDTO getPostById(String pid) {
        Long postId = Long.parseLong(pid);
        PostEntity post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("This post does not exist"));
        PostRespDTO postRespDTO = modelMapper.map(post, PostRespDTO.class);
        postRespDTO.setResourceURL("http://localhost:" + configPropertiesService.getServerPort() + "/posts/download/" + postRespDTO.getId());
        setReactions(postRespDTO);
        return postRespDTO;
    }

    @Override
    public File takeFile(String pid) {
        String filePath = postRepository.takeFilePath(Long.parseLong(pid));
        return fileStoreServiceImpl.getFile(filePath);
    }

    @Override
    public Page<PostRespDTO> getAllPostsCategory(Long categoryId, int offset, int pageSize, String sortType) {
        validatePage(offset);
        List<PostRespDTO> respDTOS = null;
        if (sortType.toLowerCase().equals("hot")) {
            respDTOS = dao.getAllRecentPostsByCategorySortedByReactionCount((offset - 1), pageSize, categoryId);
        } else if (sortType.toLowerCase().equals("fresh")) {
            respDTOS = dao.getAllRecentPostsByCategoryId((offset - 1), pageSize, categoryId);
        } else {
            throw new BadRequestException("No such filtering option");
        }
        return new PageImpl<>(respDTOS);
    }

    @Override
    public Page<PostRespDTO> findAllSortedByReactionCount(int offset, int pageSize) {
        validatePage(offset);
        return new PageImpl<>(dao.getAllRecentPostsSortedByReactionCount((offset - 1), pageSize));
    }

    @Override
    public Page<PostRespDTO> allPostsWithTag(String tag, int offset, int pageSize, String sortType) {
        validatePage(offset);
        List<PostRespDTO> respDTOS = null;
        TagEntity tagEntity = tagRepository.findByTagType(tag);
        if (tagEntity == null) {
            throw new NotFoundException("No such tag!");
        }
        if (sortType.toLowerCase().equals("hot")) {
            respDTOS = dao.getAllRecentPostsByTagIdSortedByReactionCount((offset - 1), pageSize, tagEntity.getId());
        } else if (sortType.toLowerCase().equals("fresh")) {
            respDTOS = dao.getAlLRecentPostsByTagId((offset - 1), pageSize, tagEntity.getId());
        } else {
            throw new BadRequestException("No such filtering option.");
        }
        return new PageImpl<>(respDTOS);
    }

    private List<TagEntity> setTagsFromPostDto(List<String> tags) {
        List<TagEntity> tagEntityList = new ArrayList<>();

        for (String type : tags) {
            String typeTag = type.toUpperCase();
            TagEntity tagEntity = new TagEntity();
            tagEntity = tagRepository.findByTagType(typeTag);

            if (tagEntity == null) {
                TagCreatedDTO tagCreatedDto = new TagCreatedDTO();
                tagCreatedDto.setTagType(typeTag);
                tagServiceImpl.createdTag(tagCreatedDto);
                tagEntity = tagRepository.findByTagType(typeTag);
            }
            tagEntityList.add(tagEntity);
        }

        return tagEntityList;
    }

    private PostReactionResponseDTO deleteReaction(UserPostReaction.PostReactionKey key) {
        UserPostReaction reaction = reactionsRepository.findById(key).orElseThrow(() -> new NotFoundException("Reaction not found."));
        PostReactionResponseDTO responseDTO = new PostReactionResponseDTO();
        responseDTO.setId(reaction.getPost().getId());
        reactionsRepository.delete(reaction);
        responseDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndPostId(reaction.getPost().getId()));
        responseDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndPostId(reaction.getPost().getId()));

        return responseDTO;
    }

    private Page<PostRespDTO> pageMappingToDTO(Page<PostEntity> postEntities) {
        return new PageImpl<>(postEntities.stream()
                .map(postEntity -> modelMapper.map(postEntity, PostRespDTO.class))
                .collect(Collectors.toList()));
    }

    private void setURL(Page<PostRespDTO> postDtos) {
        for (PostRespDTO postRespDTO : postDtos) {
            postRespDTO.setResourceURL("http://localhost:" + configPropertiesService.getServerPort() + "/posts/download/" + postRespDTO.getId());
            setReactions(postRespDTO);
        }
    }

    private void setReactions(PostRespDTO respDTO) {
        respDTO.setLikes(reactionsRepository.countAllByStatusIsTrueAndPostId(respDTO.getId()));
        respDTO.setDislikes(reactionsRepository.countAllByStatusIsFalseAndPostId(respDTO.getId()));
    }

    private void validatePage(int page) {
        if (page < 1) {
            throw new BadRequestException("Invalid page.");
        }
    }
}
