package com.serverlabb3.postms;

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
