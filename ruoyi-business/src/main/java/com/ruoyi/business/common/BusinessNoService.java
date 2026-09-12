package com.ruoyi.business.common;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.DateUtils;

/**
 * 业务单据编号生成服务
 * 规则：前缀 + yyyyMMdd + 4位当日流水（Redis 自增，按天过期）
 *
 * @author renovationops
 */
@Service
public class BusinessNoService
{
    private static final String KEY_PREFIX = "biz:no:";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 生成业务编号，如 BJ202601010001
     *
     * @param prefix 前缀（BJ/HT/RW/BX/JB/RK/CK）
     */
    public String nextNo(String prefix)
    {
        String date = DateUtils.dateTimeNow("yyyyMMdd");
        String key = KEY_PREFIX + prefix + ":" + date;
        Long seq = redisTemplate.opsForValue().increment(key, 1);
        if (seq != null && seq == 1L)
        {
            redisTemplate.expire(key, 25, TimeUnit.HOURS);
        }
        return prefix + date + String.format("%04d", seq % 10000);
    }
}
