package com.perceivedev.perceivecore.time;

import java.util.concurrent.TimeUnit;

/**
 * Allows the parsing of a duration
 */
@SuppressWarnings("unused")
public class DurationParser {

    /**
     * Small recursive parser by I Al Istannen. Bug me about it, I know it is bad!
     * <p>
     * Format:
     * <br>"xxS" ==> milliseconds
     * <br>"xxt" ==> ticks
     * <br>"xxs" ==> seconds
     * <br>"xxm" ==> minutes
     * <br>"xxh" ==> hours
     * <br>"xxd" ==> days
     *
     * @param input The input string
     *
     * @return The time in milliseconds
     *
     * @throws RuntimeException If an error occurred while parsing.
     */
    public static long parseDurationToTicks(String input) throws RuntimeException {
        return parseDuration(input) / 50;
    }

    /**
     * Small recursive parser by I Al Istannen. Bug me about it, I know it is bad!
     * <p>
     * Format:
     * <br>"xxS" ==> milliseconds
     * <br>"xxt" ==> ticks
     * <br>"xxs" ==> seconds
     * <br>"xxm" ==> minutes
     * <br>"xxh" ==> hours
     * <br>"xxd" ==> days
     *
     * @param input The input string
     *
     * @return The time in milliseconds
     *
     * @throws RuntimeException If an error occurred while parsing.
     */
    @SuppressWarnings("WeakerAccess")
    public static long parseDuration(String input) throws RuntimeException {
        return new Object() {

            private int pos = -1, ch;

            /**
             * Goes to the next char
             */
            private void nextChar() {
                ch = ++pos < input.length() ? input.charAt(pos) : -1;
            }

            /**
             * Eats a char
             *
             * @param charToEat The chat to eat
             * @return True if the char was found and eaten
             */
            private boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            public long parse() {
                nextChar();
                return parsePart();
            }

            private long parsePart() {
                long number = parseNumber();
                while (ch != -1) {
                    number += parseNumber();
                }

                return number;
            }

            private long parseNumber() {
                while (ch == ' ') {
                    nextChar();
                }
                long number;
                int start = pos;
                if (ch >= '0' && ch <= '9') {
                    while (ch >= '0' && ch <= '9') {
                        nextChar();
                    }
                    number = Long.parseLong(input.substring(start, pos));

                    //noinspection StatementWithEmptyBody
                    if (eat('S')) {
                        // well, it is already in ms
                    } else if (eat('s')) {
                        number *= 1000;
                    } else if (eat('m')) {
                        number = TimeUnit.MINUTES.toMillis(number);
                    } else if (eat('h')) {
                        number = TimeUnit.HOURS.toMillis(number);
                    } else if (eat('d')) {
                        number = TimeUnit.DAYS.toMillis(number);
                    } else if (eat('t')) {
                        number *= 50;
                    } else {
                        throw new RuntimeException("No unit given near pos " + pos + " starting at " + start);
                    }
                } else {
                    throw new RuntimeException("Unexpected char at pos " + pos + " " + ch + " '" + (char) ch + "'");
                }

                return number;
            }

        }.parse();
    }
}

