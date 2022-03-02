package com.liao.im.server.netty.thread;

import com.liao.im.common.proto.MsgProto;
import com.liao.im.server.netty.service.Processor;
import com.liao.im.server.session.ServerSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author liao
 * create at 2022:03:02  10:11
 */
@Component
@Slf4j
public class HandlerTask {
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    /**
     * 处理任务
     *
     * @param processor {@link Processor}
     * @param session   {@link ServerSession}
     * @param message   {@link com.liao.im.common.proto.MsgProto.Message}
     * @return 异步处理结果
     */
    public Future<Boolean> handle(Processor processor, ServerSession session, MsgProto.Message message) {
        return threadPoolExecutor.submit(() -> {
            final Boolean process = processor.process(session, message);
            log.debug("任务提交 结果为{}", process);
            return process;
        });
    }
}
