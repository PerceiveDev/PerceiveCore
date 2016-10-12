package com.perceivedev.perceivecore.command;

import org.bukkit.permissions.Permission;

import com.perceivedev.perceivecore.language.MessageProvider;

/**
 * A CommandNode that uses the I18N translations
 */
public abstract class TranslatedCommandNode extends AbstractCommandNode {

    private String keywordKey, keywordRegExKey, usageKey, descriptionKey;
    private MessageProvider messageProvider;

    /**
     * @param permission The Permission
     * @param keywordKey The key in the language file for the keyword
     * @param keywordRegExKey The key in the language file for the regex to match the keyword
     * @param usageKey The key in the language file for the usage
     * @param descriptionKey The key in the language file for the description
     * @param messageProvider The {@link MessageProvider}
     * @param acceptedSenders The accepted sender types
     */
    public TranslatedCommandNode(Permission permission, String keywordKey, String keywordRegExKey, String usageKey, String descriptionKey,
              MessageProvider messageProvider, CommandSenderType... acceptedSenders) {
        super(permission, acceptedSenders);

        this.keywordKey = keywordKey;
        this.keywordRegExKey = keywordRegExKey;
        this.usageKey = usageKey;
        this.descriptionKey = descriptionKey;
        this.messageProvider = messageProvider;
    }

    /**
     * @param permission The Permission
     * @param baseKey The base key. Appended will be "_keyword", "_keyword_pattern", "_usage", "_description"
     * @param messageProvider The {@link MessageProvider}
     * @param acceptedSenders The accepted sender types
     *
     * @see #TranslatedCommandNode(Permission, String, String, String, String, MessageProvider, CommandSenderType...)
     */
    public TranslatedCommandNode(Permission permission, String baseKey, MessageProvider messageProvider,
              CommandSenderType... acceptedSenders) {
        super(permission, acceptedSenders);
        this.messageProvider = messageProvider;

        this.keywordKey = baseKey + "_keyword";
        this.keywordRegExKey = baseKey + "_keyword_pattern";
        this.usageKey = baseKey + "_usage";
        this.descriptionKey = baseKey + "_description";
    }

    @Override
    public boolean isYourKeyword(String string) {
        return string.matches(messageProvider.trUncolored(keywordRegExKey));
    }

    @Override
    public String getKeyword() {
        return messageProvider.tr(keywordKey);
    }

    @Override
    public String getUsage() {
        return messageProvider.tr(usageKey);
    }

    @Override
    public String getDescription() {
        return messageProvider.tr(descriptionKey);
    }
}
