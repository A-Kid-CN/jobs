package com.baomidou.jobs.admin.service.impl;

import com.baomidou.jobs.admin.mapper.JobsInfoMapper;
import com.baomidou.jobs.admin.service.IJobsInfoService;
import com.baomidou.jobs.admin.service.IJobsLogService;
import com.baomidou.jobs.admin.service.JobsPageHelper;
import com.baomidou.jobs.cron.CronExpression;
import com.baomidou.jobs.model.JobsInfo;
import com.baomidou.jobs.trigger.JobsTrigger;
import com.baomidou.jobs.trigger.TriggerTypeEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class JobsInfoServiceImpl implements IJobsInfoService {
    @Resource
    private JobsInfoMapper jobInfoMapper;
    @Autowired
    private IJobsLogService jobsInfoService;

    @Override
    public R<IPage<JobsInfo>> page(HttpServletRequest request, JobsInfo jobsInfo) {
        return R.ok(jobInfoMapper.selectPage(
                JobsPageHelper.getPage(request), Wrappers.query(jobsInfo)
        ));
    }

    @Override
    public int count() {
        return jobInfoMapper.selectCount(null);
    }

    @Override
    public List<JobsInfo> listNextTime(long nextTime) {
        return jobInfoMapper.selectList(Wrappers.<JobsInfo>lambdaQuery()
                .le(JobsInfo::getNextTime, nextTime));
    }

    @Override
    public boolean updateById(JobsInfo jobInfo) {
        return jobInfoMapper.updateById(jobInfo) > 0;
    }

    @Override
    public boolean execute(Long id, String param) {
        return JobsTrigger.trigger(id, TriggerTypeEnum.MANUAL, -1, param);
    }

    @Override
    public boolean start(Long id) {
        JobsInfo dbJobInfo = getById(id);
        if (null == dbJobInfo) {
            return false;
        }
        JobsInfo jobsInfo = new JobsInfo();
        jobsInfo.setId(dbJobInfo.getId());

        // next trigger time (10s后生效，避开预读周期)
        long nextTriggerTime;
        try {
            nextTriggerTime = new CronExpression(dbJobInfo.getCron())
                    .getNextValidTimeAfter(new Date(System.currentTimeMillis() + 10000)).getTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return false;
        }

        jobsInfo.setStatus(1);
        jobsInfo.setLastTime(0L);
        jobsInfo.setNextTime(nextTriggerTime);
        return jobInfoMapper.updateById(jobsInfo) > 0;
    }

    @Override
    public boolean stop(Long id) {
        JobsInfo jobsInfo = new JobsInfo();
        jobsInfo.setId(id);
        jobsInfo.setStatus(0);
        jobsInfo.setLastTime(0L);
        jobsInfo.setNextTime(0L);
        return jobInfoMapper.updateById(jobsInfo) > 0;
    }

    @Override
    public boolean remove(Long id) {
        jobsInfoService.removeById(id);
        return jobInfoMapper.deleteById(id) > 0;
    }

    @Override
    public JobsInfo getById(Long id) {
        return jobInfoMapper.selectById(id);
    }
}
