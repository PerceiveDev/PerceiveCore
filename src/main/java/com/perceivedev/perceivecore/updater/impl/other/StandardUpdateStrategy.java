package com.perceivedev.perceivecore.updater.impl.other;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.perceivedev.perceivecore.reflection.ReflectionUtil;
import com.perceivedev.perceivecore.updater.UpdateStrategy;
import com.perceivedev.perceivecore.updater.UpdaterEntry;

/**
 * Some implementations for {@link UpdateStrategy}
 */
@SuppressWarnings("rawtypes")
public enum StandardUpdateStrategy implements UpdateStrategy {
    TIME(UpdaterEntry::getReleaseTime,
            (o, o2) -> {
                ChronoLocalDateTime<?> first = (ChronoLocalDateTime<?>) o;
                ChronoLocalDateTime<?> second = (ChronoLocalDateTime<?>) o2;
                return first.compareTo(second) > 0;
            },
            ChronoLocalDateTime.class),
    SEMANTIC_VERSIONING(UpdaterEntry::getVersion, (
            o, o2) -> true,
            String.class) {

        // @formatter:off
        /**
         * Matches things like in the groups [1-3]:
         * <ul>
         *     <li>2.5.21</li>
         *     <li>5.6.67</li>
         *     <li>65.32.54</li>
         *     <li>5.5.5-SNAPSHOT</li>
         * </ul>
         */
        // @formatter:on
        private final Pattern PATTERN = Pattern.compile("(\\d{1,5})\\.(\\d{1,5})\\.(\\d{1,5})(-.+)?");

        @Override
        public Object identifierFromEntry(UpdaterEntry entry) {
            String version = entry.getVersion();
            return PATTERN.matcher(version).find() ? version : null;
        }

        @Override
        public boolean isNewer(Object identifierFirst, Object identifierSecond) {
            String first = (String) identifierFirst;
            String second = (String) identifierSecond;
            return firstIsNewer(first, second);
        }

        private boolean firstIsNewer(String first, String second) {
            int[] versionsFirst = extractVersions(first);
            int[] versionsSecond = extractVersions(second);

            for (int i = 0; i < versionsFirst.length; i++) {
                if (versionsFirst[i] < versionsSecond[i]) {
                    return false;
                }
                if (versionsFirst[i] > versionsSecond[i]) {
                    return true;
                }
            }
            return false;
        }

        private int[] extractVersions(String string) {
            Matcher matcher = PATTERN.matcher(string);
            int[] versions = { 0, 0, 0 };

            if (matcher.find()) {
                for (int i = 0; i < 3; i++) {
                    versions[i] = Integer.parseInt(matcher.group(i + 1));
                }
            }

            return versions;
        }
    };

    private Function<UpdaterEntry, Object> identifierExtractor;
    private BiPredicate<Object, Object> firstIsNewer;

    private Class<?> elementType;

    StandardUpdateStrategy(Function<UpdaterEntry, Object> identifierExtractor, BiPredicate<Object, Object> firstIsNewer,
            Class<?> elementType) {
        this.identifierExtractor = identifierExtractor;
        this.firstIsNewer = firstIsNewer;
        this.elementType = elementType;
    }

    /**
     * Generates an identifier from an {@link UpdaterEntry}
     *
     * @param entry The entry to get it from
     *
     * @return The identifier or {@code null} if not possible.
     */
    @Override
    public Object identifierFromEntry(UpdaterEntry entry) {
        Objects.requireNonNull(entry, "entry can not be null!");

        return identifierExtractor.apply(entry);
    }

    /**
     * Checks if a file is newer than the other
     *
     * @param identifierFirst The identifier of the first one (Date, Name,
     *            Version, ...)
     * @param identifierSecond The version of the first second (Date, Name,
     *            Version, ...)
     *
     * @return True if the first is newer than the second
     */
    @Override
    public boolean isNewer(Object identifierFirst, Object identifierSecond) {
        Objects.requireNonNull(identifierFirst, "identifierFirst can not be null!");
        Objects.requireNonNull(identifierSecond, "identifierSecond can not be null!");

        if (!ReflectionUtil.inheritsFrom(identifierFirst.getClass(), elementType)) {
            throw new IllegalArgumentException("The first element is of type "
                    + identifierFirst.getClass().getName()
                    + " not "
                    + elementType.getName());
        }
        if (!ReflectionUtil.inheritsFrom(identifierSecond.getClass(), elementType)) {
            throw new IllegalArgumentException("The second element is of type "
                    + identifierSecond.getClass().getName()
                    + " not "
                    + elementType.getName());
        }
        return firstIsNewer.test(identifierFirst, identifierSecond);
    }
}
