package com.concise.component.datasource.mybatisplus.utils;

import com.concise.component.core.utils.SqlUtil;
import com.concise.component.core.utils.StringUtils;
import com.concise.component.datasource.mybatisplus.entity.PageDomain;
import com.concise.component.datasource.mybatisplus.entity.TableSupport;
import com.github.pagehelper.PageHelper;

/**
 * 分页工具
 * @author shenguangyang
 * @date 2021-09-08 21:14
 */
public class PageUtil {
    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }
//
//    /**
//     * 响应请求分页数据
//     */
//    @SuppressWarnings({ "rawtypes", "unchecked" })
//    public static <T> PageResponse<T> getDataTable(List<T> list) {
//        PageResponse rspData = PageResponse.buildSuccess(list);
//        rspData.setData(list);
//        if (CollectionUtils.isEmpty(list)) {
//            rspData.setTotal(0);
//            return rspData;
//        }
//        rspData.setTotal(new PageInfo(list).getTotal());
//        return rspData;
//    }
}
