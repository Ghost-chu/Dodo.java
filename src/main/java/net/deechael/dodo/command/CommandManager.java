package net.deechael.dodo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.deechael.dodo.api.MessageContext;
import net.deechael.dodo.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class CommandManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);

    private final CommandDispatcher<MessageContext> commandDispatcher;

    private final Map<String, DodoCommand> commands = new HashMap<>();

    private final Map<Pattern, DodoCommand> patterns = new HashMap<>();

    public CommandManager() {
        this.commandDispatcher = new CommandDispatcher<>();
    }

    public void register(DodoCommand command) {
        String commandName = command.getName();
        this.commands.put(commandName, command);
        this.commandDispatcher.getRoot().addChild(command.getExecutor().brigadier(commandName));
        updatePatterns();
    }

    private void updatePatterns() {
        this.patterns.clear();
        for (Entry<String, DodoCommand> entry : this.commands.entrySet()) {
            DodoCommand command = entry.getValue();
            if (command.getRegex() != null) {
                patterns.put(Pattern.compile(command.getRegex()), command);
            } else {
                String safeCommandName = StringUtils.safePattern(entry.getKey());
                for (String prefix : entry.getValue().getPrefixes()) {
                    patterns.put(Pattern.compile(prefix + safeCommandName), command);
                    for (String alias : entry.getValue().getAliases()) {
                        patterns.put(Pattern.compile(prefix + StringUtils.safePattern(alias)), command);
                    }
                }
            }
        }
    }

    public void execute(MessageContext context, String messageContent) {
        String message = messageContent;
        while (message.endsWith(" ")) {
            message = message.substring(0, message.length() - 1);
            if (message.isEmpty())
                break;
        }
        String partToBeChecked;
        if (message.contains(" ")) {
            partToBeChecked = message.split(" ")[0];
        } else {
            partToBeChecked = message;
        }
        for (Entry<Pattern, DodoCommand> entry : this.patterns.entrySet()) {
            if (entry.getKey().matcher(partToBeChecked).matches()) {
                DodoCommand command = entry.getValue();
                try {
                    this.commandDispatcher.execute(command.getName() + message.substring(partToBeChecked.length()), context);
                } catch (CommandSyntaxException e) {
                    ExceptionExecutor exceptionExecutor = command.getExceptionExecutor();
                    if (exceptionExecutor != null) {
                        exceptionExecutor.execute(context.getAuthor(), context, e);
                    } else {
                        LOGGER.error("Command error: " + e.getContext());
                    }
                }
            }
        }
    }

}
