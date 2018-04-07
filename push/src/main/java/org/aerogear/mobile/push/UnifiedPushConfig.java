package org.aerogear.mobile.push;

import static org.aerogear.mobile.core.utils.SanityCheck.nonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UnifiedPushConfig {

    /**
     * Topics in Firebase must conform to this pattern. See:
     * https://firebase.google.com/docs/reference/android/com/google/firebase/messaging/FirebaseMessaging#subscribeToTopic(java.lang.String)
     */
    private static final String FCM_TOPIC_PATTERN = "[a-zA-Z0-9-_.~%]{1,900}";

    private String alias;
    private List<String> categories = Collections.emptyList();

    /**
     * The alias is an identifier of the user of the system.
     *
     * @return alias
     *
     */
    public String getAlias() {
        return alias;
    }

    /**
     * The alias is an identifier of the user of the system.
     *
     * @param alias the new alias
     *
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * The categories specifies a channel which may be used to send messages
     *
     * @return the current categories
     */
    public List<String> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    /**
     * The categories specifies a channel which may be used to send messages
     *
     * @param categories the new categories
     */
    public void setCategories(List<String> categories) {
        nonNull(categories, "categories");
        validateCategories(categories);
        this.categories = new ArrayList<>(categories);
    }

    /**
     * Validates categories against Google's pattern.
     *
     * @param categories a group of Strings each will be validated.
     * @throws IllegalArgumentException if a category fails to match [a-zA-Z0-9-_.~%]{1,900}
     */
    private static void validateCategories(List<String> categories) {
        for (String category : categories) {
            if (!category.matches(FCM_TOPIC_PATTERN)) {
                throw new IllegalArgumentException(
                                String.format("%s does not match %s", category, FCM_TOPIC_PATTERN));
            }
        }
    }

}
