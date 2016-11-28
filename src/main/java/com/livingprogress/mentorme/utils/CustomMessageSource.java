package com.livingprogress.mentorme.utils;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Custom message source.
 */
public class CustomMessageSource extends ResourceBundleMessageSource {
    /**
     * The message source ACCESSOR.
     */
    private static final MessageSourceAccessor ACCESSOR = new MessageSourceAccessor(new CustomMessageSource());

    /**
     * Custom message source constructor.
     */
    public CustomMessageSource() {
        setBasename("locale.messages");
        setDefaultEncoding(Helper.UTF8);
    }

    /**
     * Get message.
     * @param name the message name.
     * @return the match message by name
     */
    public static String getMessage(String name) {
        return ACCESSOR.getMessage(name);
    }

    /**
     * Get message with arguments.
     * @param name the message name.
     * @param args the message arguments
     * @return the match message by name
     */
    public static String getMessage(String name, Object...args) {
        return ACCESSOR.getMessage(name, args);
    }
}
