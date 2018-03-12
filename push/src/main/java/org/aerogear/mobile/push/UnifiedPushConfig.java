package org.aerogear.mobile.push;

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
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

}
