package com.perceivedev.perceivecore.update;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

/**
 * A conversation to confirm the update
 * 
 * @author ZP4RKER
 *
 */
public class ConfirmConversation extends ValidatingPrompt {

    private boolean       conversing = true;
    private PluginUpdater updater;

    public ConfirmConversation(PluginUpdater updater) {
        this.updater = updater;
    }

    @Override
    public String getPromptText(ConversationContext context) {
        context.getForWhom().sendRawMessage("WARNING: If you are not using the latest Spigot/Bukkit"
                + " server build then this plugin could break!");
        return "Would you like to confirm the update? (Yes/No)";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext context, String input) {
        if (input.equalsIgnoreCase("yes")) {
            // Confirmed update
            updater.update((CommandSender) context.getForWhom());
        }
        conversing = false;
        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    protected boolean isInputValid(ConversationContext context, String input) {
        return (input.equalsIgnoreCase("yes") || input.equalsIgnoreCase("no"));
    }

    public boolean isConversing() {
        return conversing;
    }

}