package org.aerogear.mobile.core.utils;

/**
 * Utility class used to check that passed in parameters complies to specific rules.
 */
public class SanityCheck {

    /**
     * Utility classes are not meant to be instantiated.
     */
    private SanityCheck() {
    }

    /**
     * Checks that the passed in value is not null. If it is null, a {@link NullPointerException}
     * is thrown with a message specifying that the parameter can't be null.
     *
     * @param value current value
     * @param paramName parameter name
     * @param <T>
     * @return current value
     */
    public static <T> T nonNull(final T value, final String paramName) {
        return nonNull(value,"Parameter '%s' can't be null", paramName);
    }

    /**
     * Checks that the passed in value is not null. If it is, a {@link NullPointerException} is
     * thrown with message customMessage. If the custom message is a pattern, params will be applied
     * to that pattern
     * @see String#format(String, Object...)
     *
     * @param value current value
     * @param customMessage message to be put inside the NullPointerException. Can be a pattern.
     * @param params parameters to be applied to the customMessage pattern
     * @param <T>
     * @return the current value
     */
    public static <T> T nonNull(final T value, final String customMessage, final Object ... params) {
        if (value == null) {
            throw new NullPointerException(String.format(customMessage, params));
        }
        return value;
    }

    /**
     * Checks if the passed in value is an empty string. The string gets trimmed.
     * If it is empty, an IllegalArgumentException gets thrown with an appropriate error message.
     * @param value string to be checked
     * @param paramName parameter name to be used in the error message
     * @return the received param
     */
    public static String nonEmpty(final String value, final String paramName) {
        return nonEmpty(value, true, "'%s' can't be empty or null", paramName);
    }

    /**
     * Checks if the passed in value is an empty string. if <code>trim</code> is true, the string
     * gets trimmed.
     * If it is empty, an IllegalArgumentException gets thrown with an appropriate error message.
     * @param value string to be checked
     * @param paramName parameter name to be used in the error message
     * @param trim whether the string must be trimmed or not
     * @return the received value
     */
    public static String nonEmpty(final String value, final String paramName, final boolean trim) {
        return nonEmpty(value, trim, "%s can't be empty or null", paramName);
    }

    /**
     * Checks if the passed in value is an empty String. If it is empty an {@link IllegalArgumentException}
     * is thrown with the passed in custom message. The params are applied to the customMessage
     * according to String.format
     * @see String#format(String, Object...)
     * @param value string to be checked
     * @param customMessage custom message to be put into the exception
     * @param params parameters to be applied to the custom message
     * @return checked string
     */
    public static String nonEmpty(final String value, final String customMessage, final Object... params) {
        return nonEmpty(value, true, customMessage, (Object[]) params);
    }

    /**
     * Checks if the passed in value is an empty String. If it is empty an {@link IllegalArgumentException}
     * is thrown with the passed in custom message. The params are applied to the customMessage
     * according to String.format
     * @see String#format(String, Object...)
     * @param value string to be checked
     * @param trim whether the string must be trimmed prior checking or not
     * @param customMessage custom message to be put into the exception
     * @param params parameters to be applied to the custom message
     * @return checked string
     */
    public static String nonEmpty(final String value, final boolean trim, final String customMessage, final Object... params) {
        nonNull(value, customMessage, (Object[]) params);

        if (value.length() == 0 || (trim && value.trim().length() == 0)) {
            throw new IllegalArgumentException(String.format(customMessage, (Object[])params));
        }
        return value;
    }

    /**
     * Checks that value is of type K. If it is not, an {@link IllegalArgumentException} is thrown
     * with an appropriate message.
     *
     * @param value current value
     * @param clazz expected class
     * @param paramName name of the parameter to be used inside the message put into the exception
     * @param <T> current value type
     * @param <K> expected value type
     * @return current value
     */
    public static <T, K> T isA(final T value, final Class<K> clazz, final String paramName) {
        return isA(value, clazz, "Param '%s' must be of type '%s'. '%s' has been receive instead", paramName, clazz.getName(), value.getClass().getName());
    }

    /**
     * Checks that value is of type K. If it is not, an {@link IllegalArgumentException} is thrown
     * with an appropriate message.
     *
     * @param value current value
     * @param clazz expected class
     * @param customMessage Custom message to be put inside the {@link IllegalArgumentException}
     * @param params parameters to be applied to customMessage ({@link String#format(String, Object...)})
     * @param <T> current value type
     * @param <K> expected value type
     * @return current value
     */
    public static <T, K> T isA(final T value, final Class<K> clazz, final String customMessage, final Object...params) {
        if (!clazz.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(String.format(customMessage, params));
        }
        return value;
    }
}
