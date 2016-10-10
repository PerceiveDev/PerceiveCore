
package com.perceivedev.perceivecore.util;

public enum Unicode {

    X("\u2716"),
    X_THIN("\u2715"),
    CHECK("\u2714"),
    CHECK_THIN("\u2713"),

    CROSS("\u271e"),
    COFFEE("\u2615"),
    HEART("\u2764"),
    FEMALE("\u2640"),
    MALE("\u2642"),
    INFINITY("\u221e"),
    COLON_WHITE("\u2982"),

    PENCIL_LOWER_RIGHT("\u270e"),
    PENCIL_RIGHT("\u270f"),
    PENCIL_UPPER_RIGHT("\u2710"),

    COIN("\u26c0"),
    COIN_INVERTED("\u26c2"),
    COIN_STACK("\u26c1"),
    COIN_STACK_INVERTED("\u26c3"),

    CIRCLE_SMALL("\u2022"),

    BLOCK_THIN("\u2758"),
    BLOCK_MEDIUM("\u2759"),
    BLOCK_THICK("\u275a"),
    BLOCK_FULL("\u2588"),
    BLOCK_SMALL("\u220e"),

    DOUBLE_ANGLE_RIGHT("\u00bb"),
    DOUBLE_ANGLE_LEFT("\u00Ab"),

    TRIANGLE_BLACK_UP("\u25b2"),
    TRIANGLE_WHITE_UP("\u25b3"),
    TRIANGLE_BLACK_RIGHT("\u25b6"),
    TRIANGLE_WHITE_RIGHT("\u25b7"),
    TRIANGLE_BLACK_DOWN("\u25bc"),
    TRIANGLE_WHITE_DOWN("\u25bd"),
    TRIANGLE_BLACK_LEFT("\u25c0"),
    TRIANGLE_WHITE_LEFT("\u25c1"),

    STAR_4("\u2726"),
    STAR_WHITE_4("\u2727"),
    STAR_6("\u2736"),
    STAR_8("\u2738"),

    ARROW_TRIPLE_LINE("\u279f"),
    ARROW_ROUND("\u279c"),
    ARROW_CIRCLE("\u27b2"),

    ARROW_SMALL_LEFT("\u2190"),
    ARROW_SMALL_RIGHT("\u2192"),
    ARROW_WHITE_LEFT("\u21e6"),
    ARROW_WHITE_RIGHT("\u21e8"),

    ANGLE_BRACKET_LEFT_LIGHT("\u276c"),
    ANGLE_BRACKET_RIGHT_LIGHT("\u276d"),
    ANGLE_BRACKET_LEFT_MEDIUM("\u276e"),
    ANGLE_BRACKET_RIGHT_MEDIUM("\u276f"),
    ANGLE_BRACKET_LEFT_HEAVY("\u2770"),
    ANGLE_BRACKET_RIGHT_HEAVY("\u2771");

    private String character;

    Unicode(String character) {
        this.character = character;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return character;
    }

}
