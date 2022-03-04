package com.concise.component.datasource.mybatisplus.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.concise.component.core.entity.response.PageResponse;

/**
 * 分页响应助手
 * @author shenguangyang
 * @date 2022-01-06 20:19
 */
public class PageResponseHelper {
    public static <T> PageResponse<T> buildPage(IPage<T> page) {
        // return PageResponse.buildSuccess("操作成功", page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent());
        return null;
    }
}
