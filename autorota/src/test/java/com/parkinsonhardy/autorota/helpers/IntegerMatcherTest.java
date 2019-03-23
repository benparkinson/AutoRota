package com.parkinsonhardy.autorota.helpers;

import org.junit.Assert;
import org.junit.Test;

public class IntegerMatcherTest {

    @Test
    public void testSingleNumber() {
        IntegerMatcher im = new IntegerMatcher(1);
        Assert.assertTrue(im.matches(1));
        Assert.assertFalse(im.matches(2));
    }

    @Test
    public void testMultipleNumber() {
        IntegerMatcher im = new IntegerMatcher(1, 2);
        Assert.assertTrue(im.matches(1));
        Assert.assertTrue(im.matches(2));
        Assert.assertFalse(im.matches(4));
    }

}
