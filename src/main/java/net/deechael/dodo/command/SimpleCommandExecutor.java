package net.deechael.dodo.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.deechael.dodo.api.Member;
import net.deechael.dodo.api.MessageContext;
import net.deechael.dodo.command.brigadier.ChatArgumentType;
import net.deechael.dodo.content.TextMessage;

public interface SimpleCommandExecutor extends CommandExecutor {

    @Override
    default LiteralCommandNode<MessageContext> brigadier(String name) {
        return LiteralArgumentBuilder.<MessageContext>literal(name)
                .executes(context -> {
                    MessageContext message = context.getSource();
                    this.execute(message.getAuthor(), message, new String[0]);
                    return 1;
                }).then(RequiredArgumentBuilder.<MessageContext, TextMessage>argument("args", ChatArgumentType.chat())
                        .executes(context -> {
                            MessageContext message = context.getSource();
                            String content = ChatArgumentType.getChat(context, "args");
                            while (content.startsWith(" ")) {
                                content = content.substring(0, content.length() - 1);
                            }
                            while (content.endsWith(" ")) {
                                content = content.substring(0, content.length() - 1);
                            }
                            String[] args;
                            if (!content.isEmpty()) {
                                args = content.split(" ");
                            } else {
                                args = new String[0];
                            }
                            this.execute(message.getAuthor(), message, args);
                            return 1;
                        }))
                .build();
    }

    void execute(Member sender, MessageContext message, String[] args);

}
