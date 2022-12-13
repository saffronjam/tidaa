package com.backend.services;

import com.backend.BackendException;
import com.backend.bodies.ImageForm;
import com.backend.bodies.PostForm;
import com.backend.models.viewmodels.PostVm;
import com.backend.models.Post;
import com.backend.models.viewmodels.ConvertHelpers;
import com.backend.repos.ImageRepository;
import com.backend.repos.PostRepository;
import com.backend.repos.UserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PostService(PostRepository postRepository, UserRepository userRepository, ImageService imageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    public PostVm get(long id) {
        try {
            var postOpt = postRepository.findById(id);
            if (postOpt.isEmpty()) {
                throw new BackendException("Post not found", HttpStatus.NOT_FOUND);
            }
            return ConvertHelpers.toPostVm(postOpt.get());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch post by postId", HttpStatus.BAD_REQUEST);
        }
    }

    public List<PostVm> getByUser(long userId) {
        try {
            var postList = postRepository.findAllByUserId(userId);
            return postList.stream().map(ConvertHelpers::toPostVm).collect(Collectors.toList());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch post by userId", HttpStatus.BAD_REQUEST);
        }
    }

    public void add(PostForm postForm) {
        try {
            var creatorOpt = userRepository.findByToken(postForm.token());
            if (creatorOpt.isEmpty()) {
                throw new BackendException("Post creator user not found", HttpStatus.NOT_FOUND);
            }

            var imageId = postForm.image() == null ? -1 : imageService.add(new ImageForm(postForm.image()));
            var now = Calendar.getInstance().getTime().getTime();

            var post = new Post(postForm.content(), creatorOpt.get(), new java.sql.Timestamp(now), imageId);
            postRepository.save(post);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to create post", HttpStatus.BAD_REQUEST);
        }
    }

}
