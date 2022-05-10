package com.serverlabb2.postms;

import java.util.Date;
import java.util.List;

public record PostVm(long id, String content, Long creatorId, Date created, Long imageId,
                     String reportsId) {
}
