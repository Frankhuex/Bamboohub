package com.huex.bamboohub.request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
public class RolesAsViewerReq {
    private Long bookId;
    private List<Long> userIds;
}
