package com.serverlabb2.postms;

import java.util.List;
import java.util.stream.Collectors;

public class ConvertHelpers {
    public static PostVm toPostVm(Post post) {
        return new PostVm(
                post.getId(),
                post.getContent(),
                post.getCreatorId(),
                post.getCreated(),
                post.getImageId(),
                post.getReportsId());
    }
}
