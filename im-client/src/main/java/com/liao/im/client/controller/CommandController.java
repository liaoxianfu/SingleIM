package com.liao.im.client.controller;

/**
 * @author liao
 * create at 2022:02:28  20:45
 */

import com.liao.im.client.command.*;
import com.liao.im.client.handler.NettyClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CommandController {
    @Resource
    private ClientCommandMenu clientCommandMenu;
    @Resource
    private LoginConsoleCommand loginConsoleCommand;
    @Resource
    private ChatConsoleCommand chatConsoleCommand;
    @Resource
    private LogoutConsoleCommand logoutConsoleCommand;

    private Map<String, BaseCommand> commandMap;
    private boolean connectFlag = false;
    @Resource
    private NettyClient client;


    private ClientSession session;
    private Channel channel;

    public synchronized void notifyCommandThread() {
        this.notify();
    }

    public synchronized void waitCommandThread() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    GenericFutureListener<ChannelFuture> closeFutureListener = f -> {
        log.info("断开连接");
        channel = f.channel();
        final ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
        session.close();
        notifyCommandThread();
    };


    GenericFutureListener<ChannelFuture> connectedListener = f -> {
        final EventLoop eventLoop = f.channel().eventLoop();
        if (!f.isSuccess()) {
            log.error("连接失败,10s后重新连接");
//            执行
            eventLoop.schedule(() -> {
                try {
                    client.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 10, TimeUnit.SECONDS);
            connectFlag = false;
        } else {
            connectFlag = true;
            log.debug("服务器连接成功");
            channel = f.channel();
            session = new ClientSession(channel);
            session.setConnected(true);
            channel.closeFuture().addListener(closeFutureListener);
            notifyCommandThread();
        }
    };


    public void initCommandMap() {
        commandMap = new HashMap<>();
        commandMap.put(clientCommandMenu.getKey(), clientCommandMenu);
        commandMap.put(loginConsoleCommand.getKey(), loginConsoleCommand);
        commandMap.put(logoutConsoleCommand.getKey(), logoutConsoleCommand);
        commandMap.put(chatConsoleCommand.getKey(), chatConsoleCommand);
        clientCommandMenu.setAllCommand(commandMap);
    }

    private boolean isLogin() {
        if (session == null) {
            log.debug("还没有登录");
            return false;
        }
        return session.isLogin();
    }


    private void startConnect() {

    }


    private void startLogin(LoginConsoleCommand command) {

    }


    public void commonThreadRunning() {
        Thread.currentThread().setName("命令线程");
        while (true) {
            while (!connectFlag) {
                startConnect();
                waitCommandThread();
            }
            while (session != null) {
                final Scanner scanner = new Scanner(System.in);
                clientCommandMenu.exec(scanner);
                final String key = clientCommandMenu.getCommandInput(); // 获取输入的key
                final BaseCommand command = commandMap.get(key);
                if (command == null) {
                    System.err.println("命令输错了");
                    continue;
                }

                // 对应选择的业务操作
                switch (key) {
                    case ChatConsoleCommand.KEY:
                        command.exec(scanner);
                        // 进行操作
                        break;
                    case LoginConsoleCommand.KEY:
                        command.exec(scanner);
                        break;
                    case LogoutConsoleCommand.KEY:
                        command.exec(scanner);
                        break;
                }
            }
        }
    }
}
