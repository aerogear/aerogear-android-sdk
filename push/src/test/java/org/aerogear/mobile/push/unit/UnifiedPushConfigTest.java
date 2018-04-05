package org.aerogear.mobile.push.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.aerogear.mobile.push.UnifiedPushConfig;

public class UnifiedPushConfigTest {

    @Test
    public void testAlias() {
        String alias = "Push";

        UnifiedPushConfig unifiedPushConfig = new UnifiedPushConfig();
        unifiedPushConfig.setAlias(alias);

        assertEquals(alias, unifiedPushConfig.getAlias());
        assertSame(alias, unifiedPushConfig.getAlias());
    }

    @Test
    public void testCategoriesSeflDefenceCopy() {
        List<String> categories = new ArrayList<>();
        categories.add("AeroGear");

        UnifiedPushConfig unifiedPushConfig = new UnifiedPushConfig();
        unifiedPushConfig.setCategories(categories);

        assertEquals(1, unifiedPushConfig.getCategories().size());
        assertNotSame(categories, unifiedPushConfig.getCategories());
    }

    @Test
    public void testCategoriesWithValidNames() {
        List<String> categories = new ArrayList<>();
        categories.add("AeroGear");

        UnifiedPushConfig unifiedPushConfig = new UnifiedPushConfig();
        unifiedPushConfig.setCategories(categories);

        assertEquals(1, unifiedPushConfig.getCategories().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCategoriesWithInvalidNames() {
        List<String> categories = new ArrayList<>();
        categories.add("Red Hat");

        UnifiedPushConfig unifiedPushConfig = new UnifiedPushConfig();
        unifiedPushConfig.setCategories(categories);

        fail("It should raise an IllegalArgumentException");
    }

}
