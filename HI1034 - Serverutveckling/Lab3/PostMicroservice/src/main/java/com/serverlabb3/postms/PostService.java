package com.serverlabb3.postms;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.serverlabb3.utilities.Ip;
import com.serverlabb3.utilities.Requests;
import com.serverlabb3.utilities.exceptions.BackendException;
import com.serverlabb3.utilities.bodies.*;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final Ip ip;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PostService(PostRepository postRepository, Ip ip) {
        this.postRepository = postRepository;
        this.ip = ip;
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

    public List<PostVm> getByUserList(Long[] userIds) {
        try {
            var postList = postRepository.findAllByUserIds(userIds);
            return postList.stream().map(ConvertHelpers::toPostVm).collect(Collectors.toList());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch post by userId", HttpStatus.BAD_REQUEST);
        }
    }

    public void add(PostForm postForm) {
        try {
            // Validate user
            var userIdBody = Requests.getJsonObject(Requests.createGet(ip.getUserMs() + "/validate/" + postForm.token()), IdBody.class);
            if (userIdBody.id() == -1) {
                throw new BackendException("Invalid user token", HttpStatus.BAD_REQUEST);
            }

            // Create image
            var img = postForm.image();
            var imageId = -1L;
            if (img != null) {
                var body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(img.getName(), img.getOriginalFilename(), RequestBody.create(img.getBytes(), Requests.PNG))
                        .build();
                var imageIdBody = Requests.getJsonObject(Requests.createPost(ip.getImageMs() + "/images", body), IdBody.class);
                imageId = imageIdBody.id();
            }

            var now = Calendar.getInstance().getTime().getTime();

            boolean hasReports = postForm.reportsString() != null && postForm.reportsString().length() > 2;
            String reportsId = "-1";
            if (hasReports) {
                Type listType = new TypeToken<ArrayList<Integer>>() {
                }.getType();
                var reports = new Gson().fromJson(postForm.reportsString(), listType);
                var reportsStringBody = Requests.getJsonObject(Requests.createPost(ip.getVertxMs() + "/reportSet", RequestBody.create(new Gson().toJson(reports), Requests.JSON)), IdStringBody.class);
                reportsId = reportsStringBody.id();
            }

            var post = new Post(postForm.content(), userIdBody.id(), new java.sql.Timestamp(now), imageId, reportsId);
            postRepository.save(post);
        } catch (IOException | DataAccessException ignored) {
            throw new BackendException("Failed to create post", HttpStatus.BAD_REQUEST);
        }
    }
}
