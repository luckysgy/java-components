package com.concise.component.storage.common.partupload;

import lombok.Data;

import java.util.Map;

/**
 * @author shenguangyang
 * @date 2021-10-16 16:37
 */
@Data
public class MultiPartUploadInit {
    /**
     * 上次id
     */
    private String uploadId;
    /**
     * 上传url
     */
    private Map<String, String> uploadUrls;
}
