package com.ruoyi.business.remind.channel;

/**
 * 提醒通知渠道接口
 *
 * 提醒到点后由 {@link RemindNotifyDispatcher} 分发给所有已注册渠道。
 * 后期接入短信、邮件等新渠道时，只需新增一个实现本接口的 @Component 即可，
 * 无需改动扫描与分发逻辑。
 *
 * @author renovationops
 */
public interface RemindNotifyChannel
{
    /**
     * 渠道标识（如 inner 站内信 / sms 短信 / mail 邮件）
     */
    String getChannel();

    /**
     * 发送通知
     *
     * @param message 渠道无关的统一消息体（含接收人姓名/手机号，可直接用于短信等渠道）
     */
    void send(RemindNotifyMessage message);
}
