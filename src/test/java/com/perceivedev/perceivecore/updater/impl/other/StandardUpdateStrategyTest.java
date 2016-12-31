package com.perceivedev.perceivecore.updater.impl.other;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.perceivedev.perceivecore.updater.UpdaterEntry;

/**
 * Tests the Standard update strategies
 */
public class StandardUpdateStrategyTest {

    @Test
    public void identifierFromEntry() throws Exception {
        {
            StandardUpdateStrategy strategy = StandardUpdateStrategy.TIME;
            LocalDateTime middle = LocalDateTime.now();
            LocalDateTime less = middle.minusSeconds(20);
            LocalDateTime more = middle.plusSeconds(20);

            UpdaterEntry lessEntry = new UpdaterEntry("", "", less, null);
            UpdaterEntry middleEntry = new UpdaterEntry("", "", middle, null);
            UpdaterEntry moreEntry = new UpdaterEntry("", "", more, null);

            Assert.assertEquals(less, strategy.identifierFromEntry(lessEntry));
            Assert.assertEquals(middle, strategy.identifierFromEntry(middleEntry));
            Assert.assertEquals(more, strategy.identifierFromEntry(moreEntry));
        }

        {
            StandardUpdateStrategy strategy = StandardUpdateStrategy.SEMANTIC_VERSIONING;
            String middle = "1.5.0";
            String less = "1.4.0";
            String more = "1.6.0";

            UpdaterEntry lessEntry = new UpdaterEntry("", less, LocalDateTime.now(), null);
            UpdaterEntry middleEntry = new UpdaterEntry("", middle, LocalDateTime.now(), null);
            UpdaterEntry moreEntry = new UpdaterEntry("", more, LocalDateTime.now(), null);

            Assert.assertEquals(less, strategy.identifierFromEntry(lessEntry));
            Assert.assertEquals(middle, strategy.identifierFromEntry(middleEntry));
            Assert.assertEquals(more, strategy.identifierFromEntry(moreEntry));
        }
    }

    @Test
    public void isNewer() throws Exception {
        {
            StandardUpdateStrategy strategy = StandardUpdateStrategy.TIME;
            LocalDateTime middle = LocalDateTime.now();
            LocalDateTime less = middle.minusSeconds(20);
            LocalDateTime more = middle.plusSeconds(20);

            Assert.assertTrue(strategy.isNewer(more, middle));
            Assert.assertTrue(strategy.isNewer(more, less));
            Assert.assertTrue(strategy.isNewer(middle, less));

            Assert.assertFalse(strategy.isNewer(less, middle));
            Assert.assertFalse(strategy.isNewer(less, more));
            Assert.assertFalse(strategy.isNewer(middle, more));

            Assert.assertFalse(strategy.isNewer(less, less));
            Assert.assertFalse(strategy.isNewer(middle, middle));
            Assert.assertFalse(strategy.isNewer(more, more));
        }

        {
            StandardUpdateStrategy strategy = StandardUpdateStrategy.SEMANTIC_VERSIONING;
            int[] middleNumbers = ThreadLocalRandom.current().ints(3, 0, 2000).toArray();

            String middle = versionFromInts(middleNumbers);
            for (int i = 0; i < 3; i++) {
                String less = versionFromInts(modifiedCopy(middleNumbers, i, -1));
                String more = versionFromInts(modifiedCopy(middleNumbers, i, +1));

                Assert.assertTrue(strategy.isNewer(middle, less));
                Assert.assertTrue(strategy.isNewer(more, less));
                Assert.assertTrue(strategy.isNewer(more, middle));

                Assert.assertFalse(strategy.isNewer(less, middle));
                Assert.assertFalse(strategy.isNewer(less, more));
                Assert.assertFalse(strategy.isNewer(middle, more));

                Assert.assertFalse(strategy.isNewer(less, less));
                Assert.assertFalse(strategy.isNewer(middle, middle));
                Assert.assertFalse(strategy.isNewer(more, more));
            }
        }
    }

    private int[] modifiedCopy(int[] array, int pos, int mod) {
        int[] copy = Arrays.copyOf(array, array.length);
        copy[pos] = copy[pos] + mod;
        return copy;
    }

    private String versionFromInts(int[] ints) {
        return Arrays.stream(ints)
                .boxed()
                .map(Object::toString)
                .collect(Collectors.joining("."));
    }

}