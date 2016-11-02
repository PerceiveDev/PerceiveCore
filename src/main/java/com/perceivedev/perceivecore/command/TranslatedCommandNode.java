package com.perceivedev.perceivecore.command;

import org.bukkit.permissions.Permission;

import com.perceivedev.perceivecore.language.MessageProvider;

/**
 * A CommandNode that uses the I18N translations
 * <p>
 * <br>
 * <b>Needs a few language keys:</b>
 * <ul>
 * <li>baseKey.keyword</li>
 * <li>baseKey.keyword.pattern</li>
 * <li>baseKey.usage</li>
 * <li>baseKey.description</li>
 * <li>baseKey.name</li>
 * </ul>
 */
public abstract class TranslatedCommandNode extends AbstractCommandNode {

    private String          keywordKey, keywordRegExKey, usageKey, descriptionKey, nameKey;
    private MessageProvider messageProvider;

    /**
     * <b>Some language keys are needed. Look at the
     * {@link TranslatedCommandNode} javadoc</b>
     *
     * @param permission The Permission
     * @param keywordKey The key in the language file for the keyword
     * @param keywordRegExKey The key in the language file for the regex to
     *            match the keyword
     * @param usageKey The key in the language file for the usage
     * @param descriptionKey The key in the language file for the description
     * @param nameKey The key in the language file for the name
     * @param messageProvider The {@link MessageProvider}
     * @param acceptedSenders The accepted sender types
     */
    public TranslatedCommandNode(Permission permission, String keywordKey, String keywordRegExKey, String usageKey, String descriptionKey,
            String nameKey,
            MessageProvider messageProvider, CommandSenderType... acceptedSenders) {
        super(permission, acceptedSenders);

        this.keywordKey = keywordKey;
        this.keywordRegExKey = keywordRegExKey;
        this.usageKey = usageKey;
        this.descriptionKey = descriptionKey;
        this.messageProvider = messageProvider;
    }

    /**
     * <b>Some language keys are needed. Look at the
     * {@link TranslatedCommandNode} javadoc</b>
     *
     * @param permission The Permission
     * @param baseKey The base key.
     * @param messageProvider The {@link MessageProvider}
     * @param acceptedSenders The accepted sender types
     *
     * @see #TranslatedCommandNode(Permission, String, String, String, String,
     *      String, MessageProvider, CommandSenderType...)
     */
    public TranslatedCommandNode(Permission permission, String baseKey, MessageProvider messageProvider,
            CommandSenderType... acceptedSenders) {
        super(permission, acceptedSenders);
        this.messageProvider = messageProvider;

        this.keywordKey = baseKey + ".keyword";
        this.keywordRegExKey = baseKey + ".keyword.pattern";
        this.usageKey = baseKey + ".usage";
        this.descriptionKey = baseKey + ".description";
        this.nameKey = baseKey + ".name";
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

    @Override
    public String getName() {
        return messageProvider.tr(nameKey);
    }

    /**
     * Returns the {@link MessageProvider}
     *
     * @return The {@link MessageProvider}
     */
    protected MessageProvider getMessageProvider() {
        return messageProvider;
    }
}
