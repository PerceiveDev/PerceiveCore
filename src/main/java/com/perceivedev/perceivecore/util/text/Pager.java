package com.perceivedev.perceivecore.util.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.command.CommandSender;

import com.perceivedev.perceivecore.language.MessageProvider;

/** Pages something. */
public class Pager {

    /**
     * Returns the wanted page.
     * <p>
     * The different language keys are explained here:
     * {@link Page#send(CommandSender, MessageProvider)}
     *
     * @param options The options.
     * @param all All the Strings
     *
     * @return The resulting page
     */
    @Nonnull
    public static Page getPageFromStrings(@Nonnull Options options, @Nonnull List<String> all) {
        Objects.requireNonNull(options, "Options can not be null");
        Objects.requireNonNull(all, "'all' can not be null");

        return getPageFromFilterable(options, all.stream().map(StringFilterable::new).collect(Collectors.toList()));
    }

    /**
     * Returns the wanted page.
     * <p>
     * The different language keys are explained here:
     * {@link Page#send(CommandSender, MessageProvider)}
     *
     * @param options The options.
     * @param all All the Strings
     *
     * @return The resulting page
     */
    @Nonnull
    public static Page getPageFromFilterable(@Nonnull Options options, @Nonnull List<PagerFilterable> all) {
        Objects.requireNonNull(options, "Options can not be null");
        Objects.requireNonNull(all, "'all' can not be null");

        List<PagerFilterable> list = filter(options, all);
        return slice(list, options.getEntriesPerPage(), options.getPageIndex());
    }

    /**
     * Returns the page out of the list.
     *
     * @param all All of the Strings
     * @param entriesPerPage The entries per page
     * @param pageIndex Zero based page number. Will be corrected if too small
     *            or big.
     *
     * @return The resulting page
     */
    @Nonnull
    private static Page slice(@Nonnull List<PagerFilterable> all, int entriesPerPage, int pageIndex) {
        Objects.requireNonNull(all, "'all' can not be null");

        int pageAmount = (int) Math.ceil(all.size() / (double) entriesPerPage);

        if (pageAmount == 0) {
            return new Page(1, 0, Collections.emptyList());
        }

        if (pageIndex < 0 || pageIndex >= pageAmount) {
            pageIndex = pageIndex < 0 ? 0 : pageAmount - 1;
        }

        List<PagerFilterable> entries = all.subList(
                pageIndex * entriesPerPage,
                Math.min((pageIndex + 1) * entriesPerPage, all.size()));

        return new Page(pageAmount, pageIndex,
                entries.stream()
                        .flatMap(filterable -> filterable.getAllLines().stream())
                        .collect(Collectors.toList()));
    }

    /**
     * @param options The options to use
     * @param all All the {@link PagerFilterable} to filter
     *
     * @return The filtered list
     */
    @Nonnull
    private static List<PagerFilterable> filter(@Nonnull Options options, @Nonnull List<PagerFilterable> all) {
        Objects.requireNonNull(options, "Options can not be null");
        Objects.requireNonNull(all, "'all' can not be null");

        return all.stream()
                .filter(pagerFilterable -> pagerFilterable.accepts(options))
                .collect(Collectors.toList());
    }

    /** An object filterable by the Pager */
    public interface PagerFilterable {
        /**
         * @param options The options to use
         *
         * @return True if this object should pass
         */
        boolean accepts(Options options);

        /** @return All the lines this object has */
        @Nonnull
        List<String> getAllLines();
    }

    /** A small wrapper for a normal String */
    private static class StringFilterable implements PagerFilterable {
        private String string;

        /** @param string The String */
        private StringFilterable(String string) {
            Objects.requireNonNull(string, "String cannot be null!");

            this.string = string;
        }

        @Override
        public boolean accepts(@Nonnull Options options) {
            Objects.requireNonNull(options, "Options can not be null");

            return options.matchesPattern(string);
        }

        @Nonnull
        @Override
        public List<String> getAllLines() {
            return Collections.singletonList(string);
        }
    }

    /**
     * The options class. Use the {@link Options.Builder} class to obtain one (
     * {@link #builder()}).
     */
    @SuppressWarnings("WeakerAccess")
    public static class Options {
        private int entriesPerPage;
        private int pageIndex;
        private Set<SearchMode> searchModes;
        private String searchPattern;

        private Options(int entriesPerPage, int pageIndex,
                @Nonnull Set<SearchMode> searchModes, @Nonnull String searchPattern) {

            Objects.requireNonNull(searchModes, "SearchModes can not be null");
            Objects.requireNonNull(searchPattern, "searchPattern can not be null");

            this.entriesPerPage = entriesPerPage;
            this.pageIndex = pageIndex;
            this.searchModes = searchModes.isEmpty() ? EnumSet.noneOf(SearchMode.class) : EnumSet.copyOf(searchModes);
            this.searchPattern = searchPattern;
        }

        /**
         * The amount of entries on one page
         *
         * @return The entries per page
         */
        public int getEntriesPerPage() {
            return entriesPerPage;
        }

        /**
         * The index of the page
         *
         * @return The index of the page
         */
        public int getPageIndex() {
            return pageIndex;
        }

        /**
         * Checks if the String is accepted by the search pattern
         *
         * @param test The String to test
         *
         * @return True if the string matched one (or more) pattern(s)
         *
         * @throws NullPointerException if <code>test</code> is null
         */
        public boolean matchesPattern(String test) {
            Objects.requireNonNull(test, "test can not be null");

            return searchModes.stream().anyMatch(mode -> mode.accepts(test, searchPattern));
        }

        /**
         * Creates a new Builder
         *
         * @return The Builder
         */
        @Nonnull
        public static Builder builder() {
            return new Builder();
        }

        @Override
        public String toString() {
            return "Options{" +
                    "entriesPerPage=" + entriesPerPage +
                    ", pageIndex=" + pageIndex +
                    ", searchModes=" + searchModes +
                    ", searchPattern='" + searchPattern + '\'' +
                    '}';
        }

        /** The Builder of the {@link Options} object. */
        public static final class Builder {

            private int entriesPerPage = 10;
            private int pageIndex = 0;
            private Set<SearchMode> searchModes = EnumSet.of(SearchMode.CONTAINS);
            private String searchPattern = "";

            /** No instantiation from outside */
            private Builder() {
            }

            /**
             * The entries per page
             *
             * @param entriesPerPage The entries per page
             *
             * @return This Builder
             */
            @Nonnull
            public Builder setEntriesPerPage(int entriesPerPage) {
                this.entriesPerPage = entriesPerPage;

                return this;
            }

            /**
             * The index of the page. 0 - max pages
             *
             * @param pageIndex The page index
             *
             * @return This Builder
             */
            @Nonnull
            public Builder setPageIndex(int pageIndex) {
                this.pageIndex = pageIndex;

                return this;
            }

            /**
             * Sets the {@link SearchMode}s. If any of these match, it will be
             * shown.
             *
             * @param searchModes The {@link SearchMode}s. Must not be empty.
             *
             * @return This Builder
             *
             * @throws IllegalArgumentException if searchModes is empty.
             * @throws NullPointerException if searchModes is null
             */
            @Nonnull
            public Builder setSearchModes(@Nonnull Set<SearchMode> searchModes) {
                Objects.requireNonNull(searchModes, "search modes can not be null");

                if (searchModes.isEmpty()) {
                    throw new IllegalArgumentException("searchModes is empty");
                }
                this.searchModes = EnumSet.copyOf(searchModes);

                return this;
            }

            /**
             * Sets the {@link SearchMode}s. If any of these match, it will be
             * shown.
             *
             * @param first The first search mode
             * @param rest The other search modes
             *
             * @return This Builder
             *
             * @throws NullPointerException if first or rest is null
             * @see #setSearchModes(Set)
             */
            @Nonnull
            public Builder setSearchModes(@Nonnull SearchMode first, @Nonnull SearchMode... rest) {
                Objects.requireNonNull(first, "first can not be null");
                Objects.requireNonNull(rest, "rest can not be null");

                setSearchModes(EnumSet.of(first, rest));

                return this;
            }

            /**
             * Adds a {@link SearchMode}. If any of these match, it will be
             * shown.
             *
             * @param mode The {@link SearchMode} to add
             *
             * @return This Builder
             *
             * @throws NullPointerException if mode is null
             */
            @Nonnull
            public Builder addSearchMode(@Nonnull SearchMode mode) {
                Objects.requireNonNull(mode, "mode can not be null");

                searchModes.add(mode);

                return this;
            }

            /**
             * The pattern to search. Will be searched for using the specified
             * {@link SearchMode}s
             *
             * @param searchPattern The pattern to search
             *
             * @return This Builder
             */
            @Nonnull
            public Builder setSearchPattern(@Nonnull String searchPattern) {
                Objects.requireNonNull(searchPattern, "searchPattern can not be null");

                this.searchPattern = searchPattern;

                return this;
            }

            /**
             * Builds the options.
             *
             * @return The resulting Options
             */
            @Nonnull
            public Options build() {
                return new Options(entriesPerPage, pageIndex, searchModes, searchPattern);
            }
        }
    }

    /** The search mode */
    @SuppressWarnings("WeakerAccess")
    public enum SearchMode {
        /** The string is contained */
        CONTAINS(String::contains),
        /** The string is contained, ignoring case */
        CONTAINS_IGNORE_CASE((test, pattern) -> test.toLowerCase().contains(pattern.toLowerCase())),
        /** The strings are equal */
        EQUALS(String::equals),
        /** The strings are equal, ignoring case */
        EQUALS_IGNORE_CASE(String::equalsIgnoreCase),
        /** The regular expression matches */
        REGEX_MATCHES(String::matches),
        /** The regular expression matches, no matter the case */
        REGEX_MATCHES_CASE_INSENSITIVE((test, pattern) -> Pattern
                .compile(pattern, Pattern.CASE_INSENSITIVE)
                .matcher(test)
                .matches()),
        /** The regular expression can be found in the string */
        REGEX_FIND((test, pattern) -> Pattern
                .compile(pattern)
                .matcher(test)
                .find()),
        /**
         * The regular expression can be found in the string, no matter the
         * case
         */
        REGEX_FIND_CASE_INSENSITIVE((test, pattern) -> Pattern
                .compile(pattern, Pattern.CASE_INSENSITIVE)
                .matcher(test)
                .find());

        /** The first one is the String to test, the second the pattern */
        private BiFunction<String, String, Boolean> accept;

        /**
         * @param accept Whether the String is accepted, using the second param
         *            as pattern
         */
        SearchMode(BiFunction<String, String, Boolean> accept) {
            this.accept = accept;
        }

        /**
         * Checks if this {@link SearchMode} matches a String
         *
         * @param string The String to test
         * @param pattern The pattern to match against
         *
         * @return True if it matches using this {@link SearchMode}
         *
         * @throws NullPointerException if any parameter is null
         */
        public boolean accepts(@Nonnull String string, @Nonnull String pattern) {
            Objects.requireNonNull(string, "string can not be null");
            Objects.requireNonNull(pattern, "pattern can not be null");

            return accept.apply(string, pattern);
        }
    }

    /** A displayable page */
    public static class Page {
        private final int maxPages;
        private final int pageIndex;
        private final List<String> entries;
        private final String headerKey;
        private final String footerKey;

        /**
         * The language Keys can be found in the
         * {@link #send(CommandSender, MessageProvider)} method
         *
         * @param maxPages The amount of pages it would give, at this depth
         * @param pageIndex The page number of this page
         * @param entries The entries of this page
         *
         * @throws NullPointerException if any parameter is null
         * @see #Page(int, int, List, String, String) #Page(int, int, List,
         *      String, String) with the default header and footer
         */
        private Page(int maxPages, int pageIndex, @Nonnull List<String> entries) {
            this(maxPages, pageIndex, entries, "pager_header", "pager_footer");
        }

        /**
         * The language Keys can be found in the
         * {@link #send(CommandSender, MessageProvider)} method
         *
         * @param maxPages The amount of pages it would give, at this depth
         * @param pageIndex The page number of this page
         * @param entries The entries of this page
         * @param headerKey The language key for the header. Null for default.
         * @param footerKey The language key for the footer. Null for default.
         *
         * @throws NullPointerException if any parameter is null
         */
        private Page(int maxPages, int pageIndex, @Nonnull List<String> entries, @Nonnull String headerKey, @Nonnull String footerKey) {
            Objects.requireNonNull(entries, "Entries can not be null");
            Objects.requireNonNull(headerKey, "The header key can not be null");
            Objects.requireNonNull(footerKey, "The footer key can not be null");

            this.maxPages = maxPages;
            this.pageIndex = pageIndex;
            this.entries = new ArrayList<>(entries);
            this.headerKey = headerKey;
            this.footerKey = footerKey;
        }

        /**
         * Returns all the entries of the page
         *
         * @return The entries of the page. Unmodifiable
         */
        @Nonnull
        public List<String> getEntries() {
            return Collections.unmodifiableList(entries);
        }

        /**
         * Returns the index of this page
         *
         * @return The index of this page
         */
        public int getPageIndex() {
            return pageIndex;
        }

        /**
         * Returns the number of pages
         *
         * @return The amount of pages
         */
        public int getMaxPages() {
            return maxPages;
        }

        /**
         * Sends the page
         * <ul>
         * <li>Defaults:
         * <ul>
         * <li>"pager_header" {@code ==>} The header. The key can be customized
         * via the
         * constructor.
         * <ul>
         * <li>{0} {@code ==>} The current page</li>
         * <li>{1} {@code ==>} The amount of pages</li>
         * </ul>
         * </li>
         * <li>"pager_footer" {@code ==>} The footer. The key can be customized
         * via the
         * constructor.
         * <ul>
         * <li>{0} {@code ==>} The current page</li>
         * <li>{1} {@code ==>} The amount of pages</li>
         * </ul>
         * </li>
         * </ul>
         * </li>
         * </ul>
         *
         * @param sender The {@link CommandSender} to send to
         * @param language The {@link MessageProvider} to use
         *
         * @throws NullPointerException if sender or language is null
         */
        public void send(@Nonnull CommandSender sender, @Nonnull MessageProvider language) {
            Objects.requireNonNull(sender, "Sender can not be null");
            Objects.requireNonNull(language, "Language can not be null");

            sender.sendMessage(language.trOrDefault(headerKey,
                    "\n&a&l+&8&m-------------&a&l Page &8(&a{0}&8/&2{1}&8) &8&m----------------&a&l+\n ",
                    pageIndex + 1, maxPages));
            entries.forEach(s -> sender.sendMessage(TextUtils.colorize(s)));
            sender.sendMessage(language.trOrDefault(footerKey,
                    "\n&a&l+&8&m-----------------&8 (&a{0}&8/&2{1}&8) &8&m------------------&a&l+\n ",
                    pageIndex + 1, maxPages));
        }
    }
}
