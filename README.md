# Dodo.java - Ghost Fork

此 Fork 基于上游仓库，做出以下改进：

* 升级至 Dodo API v2
* 修复了 Windows 平台下文本乱码问题（指定 UTF-8 编码）
* 使用 slf4j api 替换 logback 日志引擎，允许接入任何自己的日志引擎
* 移除 Brigadier 包，转为使用 Maven 依赖
* 升级至 Java 17
* 实现了群组成员的积分 API
* 修复了私信 API 无法使用的问题
* 增强了私信事件的 API
* 新增了帖子 API
* 在内存中缓存聊天消息，允许用户通过 `message_id` 获取本次Bot会话中的历史消息

---

# Dodo.java
渡渡语音的Java SDK

我看现在好像没有几个SDK有好的结构的\
这个项目将会参考部分开黑啦SDK的结构

本项目与[KOOK.java](https://www.github.com/DeeChael/KOOK.java)同步开发，结构类似，请勿见怪

2022.8.24 16:10更新：现在基本做完了，已经可以使用了

## 吹个水
我原本是专注于KOOK机器人开发的\
但是啊，8.23号晚上我闲着没事干看了一眼渡渡的开发文档\
一看，这个卡片消息做的已经跟discord差不多了，KOOK一直不更新卡片的功能，我一看，这好啊\
翻了一下别人做的SDK，我看着有点麻烦，而且不合我口味，于是无聊写了这个SDK

## TODO List
~~1. 指令系统 (Brigadier 和 Simple两种注册方法，后续会添加Annotation方法)~~  **2022.8.24 17:30 已更新**

## 快速上手
配置文件：
```yaml
client-id: 00000000
token: "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
```
```java
package net.deechael.dodo.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.deechael.dodo.api.*;
import net.deechael.dodo.command.BrigadierCommandExecutor;
import net.deechael.dodo.command.DodoCommand;
import net.deechael.dodo.command.SimpleCommandExecutor;
import net.deechael.dodo.configuration.Configuration;
import net.deechael.dodo.configuration.file.YamlConfiguration;
import net.deechael.dodo.content.Message;
import net.deechael.dodo.content.TextMessage;
import net.deechael.dodo.event.EventHandler;
import net.deechael.dodo.event.Listener;
import net.deechael.dodo.event.channel.ChannelMessageEvent;
import net.deechael.dodo.event.channel.MessageReactionEvent;
import net.deechael.dodo.impl.DodoBot;
import net.deechael.dodo.types.MessageType;

import java.io.File;
import java.util.Arrays;

public class TestBot implements Listener /* 继承监听器 */{

    @EventHandler // 该Annotation表示下面的方法是一个监听器方法
    public void onMessage(ChannelMessageEvent event /* 这里只能有一个参数，并且是一个Event */) {
        MessageContext context = event.getContext(); // 获取消息的上下文信息
        Island island = context.getIsland(); // 获取消息发送的群组
        Channel channel = context.getChannel(); // 获取消息发送的频道
        Member member = event.getMember(); // 获取发送消息的用户
        Message bodyContent = event.getBody(); // 获取消息的内容
        if (bodyContent.getType() == MessageType.TEXT) { // 如果是纯文本内容
            String content = bodyContent.get().getAsJsonObject().get("content").getAsString(); // 通过Gson库获得消息的纯文本内容
            //context.reply(new TextMessage("你发送了：" + content)); // 回复用户一个纯文本内容
        }
    }

    @EventHandler
    public void onReaction(MessageReactionEvent event) {
        // 当机器人发送的消息被添加了回应时触发
    }

    public static void main(String[] args) {
        // 打开配置文件里面
        Configuration configuration = YamlConfiguration.loadConfiguration(new File("test-config.yml"));
        // 创建一个Bot对象，第一个参数为clientId，第二个参数为token
        Bot bot = new DodoBot(configuration.getInt("client-id"), configuration.getString("token"));
        // 注册事件监听器
        bot.addEventListener(new TestBot());
        // 注册指令，Brigadier格式注册方法，因为Brigadier指令创建时要求输入名称，所以只需要传入一个执行器即可
        bot.registerCommand(new DodoCommand(new BrigadierCommandExecutor(LiteralArgumentBuilder.<MessageContext>literal("brigadier")
                .then(RequiredArgumentBuilder.<MessageContext, String>argument("name", StringArgumentType.string())
                        .executes(context -> {
                            MessageContext messageContext = context.getSource();
                            messageContext.reply(new TextMessage("你输入了一个参数：" + StringArgumentType.getString(context, "name")));
                            return 1;
                        }))
                .executes(context -> {
                    MessageContext messageContext = context.getSource();
                    messageContext.reply(new TextMessage("你执行了一个无参指令"));
                    return 1;
                })
                .build())));
        // Bukkit风格的Simple指令注册方法，第一个参数为指令名称，第二个为执行器
        bot.registerCommand(new DodoCommand("simple", new SimpleCommandExecutor() {
            @Override
            public void execute(Member sender, MessageContext message, String[] args) {
                System.out.println(Arrays.toString(args));
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("a")) {
                        message.reply(new TextMessage("success!"));
                    }
                }
            }
        }));
        // 添加任务，会在成功连接websocket以后运行
        /*
        bot.runAfter(() -> {
            // 将本地图片“test.png”上传至渡渡的服务器
            String url = bot.getClient().uploadImage(new File("test.png"));
            // 输出图片的url
            System.out.println(url);
        });
         */
        // 启动机器人
        bot.start();
    }

}

```