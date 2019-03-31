package com.parkinsonhardy.autorota.helpers;

import org.junit.Assert;
import org.junit.Test;

public class IntegerMatcherTest {

    @Test
    public void testSingleNumber() {
        String x = "[\n" +
                "  {\n" +
                "    \"niceName\": \"\"," +
                "    \"name\": \"\",\n" +
                "    \"unique\": false,\n" +
                "    \"params\": []\n" +
                "  },\n" +
                "  {\n" +
                "    \"niceName\": \"Balance of Average Hours across Doctors\",\n" +
                "    \"name\": \"AverageHoursBalance\",\n" +
                "    \"unique\": true,\n" +
                "    \"params\": [\n" +
                "      {\n" +
                "        \"name\": \"Weight\",\n" +
                "        \"type\": \"number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"niceName\": \"Allocate Shifts in Blocks\",\n" +
                "    \"name\": \"ShiftBlocks\",\n" +
                "    \"unique\": false,\n" +
                "    \"params\": [\n" +
                "      {\n" +
                "        \"name\": \"ShiftName\",\n" +
                "        \"type\": \"text\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"DaysInBlock\",\n" +
                "        \"type\": \"text\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"Weight\",\n" +
                "        \"type\": \"number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"niceName\": \"Balance of Shift Types across Doctors\",\n"+
                "    \"name\": \"ShiftTypeBalance\",\n" +
                "    \"unique\": true,\n" +
                "    \"params\": [\n" +
                "      {\n" +
                "        \"name\": \"Weight\",\n" +
                "        \"type\": \"number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]";;
        System.out.println(x);
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
