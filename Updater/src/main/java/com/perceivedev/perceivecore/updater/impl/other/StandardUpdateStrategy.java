package com.perceivedev.perceivecore.updater.impl.other;

import java.time.chrono.ChronoLocalDateTime;
import java.util.Objects;
import java.util.function.BiFunction;
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
    @SuppressWarnings("unused")
    TIME(UpdaterEntry::getReleaseTime,
            (o, o2) -> {
                ChronoLocalDateTime<?> first = (ChronoLocalDateTime<?>) o;
                ChronoLocalDateTime<?> second = (ChronoLocalDateTime<?>) o2;
                return first.compareTo(second);
            },
            ChronoLocalDateTime.class),
    @SuppressWarnings("unused")
    SEMANTIC_VERSIONING(UpdaterEntry::getVersion, (
            o, o2) -> 1,
            String.class) {

        private final Pattern PATTERN = Pattern.compile("(\\d{1,5})");

        private final Pattern IS_VALID_IDENTIFIER_PATTERN = Pattern.compile("(\\d+)(\\.(\\d+))*(-.+)?");

        @Override
        public Object identifierFromEntry(UpdaterEntry entry) {
            String version = entry.getVersion();
            return IS_VALID_IDENTIFIER_PATTERN.matcher(version).find() ? version : null;
        }

        @Override
        public int compare(Object identifierFirst, Object identifierSecond) {
            String first = (String) identifierFirst;
            String second = (String) identifierSecond;
            return compare(first, second);
        }

        private int compare(String first, String second) {
            int[] versionsFirst = extractVersions(first);
            int[] versionsSecond = extractVersions(second);

            for (int i = 0; i < versionsFirst.length; i++) {
                if (versionsFirst[i] < versionsSecond[i]) {
                    // is older
                    return -1;
                }
                if (versionsFirst[i] > versionsSecond[i]) {
                    // is newer
                    return 1;
                }
            }
            // is same
            return 0;
        }

        private int[] extractVersions(String string) {
            Matcher matcher = PATTERN.matcher(string);
            // fill with 0s
            // Otherwise "1.5" couldn't be parsed, but it means
            // "1.5.0"
            int[] versions = {0, 0, 0};

            for (int counter = 0; matcher.find(); counter++) {
                versions[counter] = Integer.parseInt(matcher.group(1));
            }

            return versions;
        }
    };

    private Function<UpdaterEntry, Object> identifierExtractor;
    private BiFunction<Object, Object, Integer> firstIsNewer;

    private Class<?> elementType;

    StandardUpdateStrategy(Function<UpdaterEntry, Object> identifierExtractor, BiFunction<Object, Object, Integer>
            firstIsNewer,
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
     * Version, ...)
     * @param identifierSecond The version of the first second (Date, Name,
     * Version, ...)
     *
     * @return True if the first is newer than the second
     */
    @Override
    public int compare(Object identifierFirst, Object identifierSecond) {
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
        return firstIsNewer.apply(identifierFirst, identifierSecond);
    }
}
