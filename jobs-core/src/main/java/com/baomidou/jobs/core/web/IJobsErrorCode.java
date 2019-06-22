package com.baomidou.jobs.core.web;

/**
 * REST API 错误码接口
 *
 * @author xxl jobob
 * @since 2019-06-08
 */
public interface IJobsErrorCode {

    /**
     * 错误编码 -1、失败 0、成功
     */
    int getCode();

    /**
     * 错误描述
     */
    String getMsg();
}
