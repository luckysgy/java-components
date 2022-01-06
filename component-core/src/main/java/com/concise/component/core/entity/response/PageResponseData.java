package com.concise.component.core.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author shenguangyang
 * @date 2022-01-06 19:53
 */
@Data
public class PageResponseData<T> implements Serializable {
    private Collection<T> list;
    private Long total;
    private Long pageSize;
    private Long pageNum;
}
