package com.serverlabb3.postms;

import java.util.Date;

public record PostVm(long id, String content, Long creatorId, Date created, Long imageId,
                     String reportsId) {
}
