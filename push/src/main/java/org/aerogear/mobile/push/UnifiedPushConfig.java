package org.aerogear.mobile.push;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UnifiedPushConfig {

    private String alias;
    private List<String> categories;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getCategories() {
        if (categories == null) {
            return new ArrayList<>();
        }
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<String> categories) {
        Collections.copy(this.categories, categories);
    }

}
