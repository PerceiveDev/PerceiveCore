package com.perceivedev.perceivecore.time;

import static com.perceivedev.perceivecore.AssertUtil.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/** A test for the duration parser */
public class DurationParserTest {

    private static long getSuffixValue(long value, String suffix) {
        switch (suffix) {
            case "S": {
                return value;
            }
            case "t": {
                return value * 50;
            }
            case "s": {
                return (value * 1000);
            }
            case "m": {
                return TimeUnit.MINUTES.toMillis(value);
            }
            case "h": {
                return TimeUnit.HOURS.toMillis(value);
            }
            case "d": {
                return TimeUnit.DAYS.toMillis(value);
            }
        }
        return 0;
    }

    @Test
    public void parseDurationToTicks() throws Exception {
        for (int i = 0; i < 30000; i++) {
            Object[] random = createStringWithMsValue();
            long number = (long) random[1];
            long res = DurationParser.parseDurationToTicks((String) random[0]);
            Assert.assertEquals("Actual: '" + (number / 50) + "' Got: '" + res + "' Expression: '" + random[0] + "'", number / 50, res);
        }

        assertThrows(() -> DurationParser.parseDuration(""), RuntimeException.class);
        assertThrows(() -> DurationParser.parseDuration("1z"), RuntimeException.class);
    }

    @Test
    public void parseDuration() throws Exception {
        for (int i = 0; i < 30000; i++) {
            Object[] random = createStringWithMsValue();
            long number = (long) random[1];
            long res = DurationParser.parseDuration((String) random[0]);
            Assert.assertEquals("Actual: '" + number + "' Got: '" + res + "' Expression: '" + random[0] + "'", number, res);
        }

        assertThrows(() -> DurationParser.parseDuration(""), RuntimeException.class);
        assertThrows(() -> DurationParser.parseDuration("1z"), RuntimeException.class);
    }

    private Object[] createStringWithMsValue() {
        List<String> suffixList = Arrays.asList("S", "s", "m", "h", "d", "t");
        long number = 0;
        StringBuilder expression = new StringBuilder();
        do {
            String suffix = suffixList.get(ThreadLocalRandom.current().nextInt(suffixList.size()));
            long tmpNumber = ThreadLocalRandom.current().nextLong(100);
            expression
                    .append(StringUtils.repeat(" ", ThreadLocalRandom.current().nextInt(20)))
                    .append(tmpNumber)
                    .append(suffix);

            number += getSuffixValue(tmpNumber, suffix);
        } while (ThreadLocalRandom.current().nextInt(10) < 8);

        return new Object[] { expression.toString(), number };
    }

}