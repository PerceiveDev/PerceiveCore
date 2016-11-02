
package com.perceivedev.perceivecore.util;

public enum Unicode {

    /** Value: '\u2716' */
    X("\u2716"),
    /** Value: '\u2715' */
    X_THIN("\u2715"),
    /** Value: '\u2714' */
    CHECK("\u2714"),
    /** Value: '\u2713' */
    CHECK_THIN("\u2713"),
    /** Value: '\u271E' */
    CROSS("\u271E"),

    /** Value: '\u2615' */
    COFFEE("\u2615"),
    /** Value: '\u2764' */
    HEART("\u2764"),
    /** Value: '\u2640' */
    FEMALE("\u2640"),
    /** Value: '\u2642' */
    MALE("\u2642"),
    /** Value: '\u221E' */
    INFINITY("\u221E"),
    /** Value: '\u2982' */
    COLON_WHITE("\u2982"),
    /** Value: '\u270E' */
    PENCIL_LOWER_RIGHT("\u270E"),

    /** Value: '\u270F' */
    PENCIL_RIGHT("\u270F"),
    /** Value: '\u2710' */
    PENCIL_UPPER_RIGHT("\u2710"),
    /** Value: '\u26C0' */
    COIN("\u26C0"),

    /** Value: '\u26C2' */
    COIN_INVERTED("\u26C2"),
    /** Value: '\u26C1' */
    COIN_STACK("\u26C1"),
    /** Value: '\u26C3' */
    COIN_STACK_INVERTED("\u26C3"),
    /** Value: '\u2022' */
    CIRCLE_SMALL("\u2022"),

    /** Value: '\u2758' */
    BLOCK_THIN("\u2758"),

    /** Value: '\u2759' */
    BLOCK_MEDIUM("\u2759"),
    /** Value: '\u275A' */
    BLOCK_THICK("\u275A"),
    /** Value: '\u2588' */
    BLOCK_FULL("\u2588"),
    /** Value: '\u220E' */
    BLOCK_SMALL("\u220E"),
    /** Value: '\u00BB' */
    DOUBLE_ANGLE_RIGHT("\u00BB"),

    /** Value: '\u00AB' */
    DOUBLE_ANGLE_LEFT("\u00AB"),
    /** Value: '\u25B2' */
    TRIANGLE_BLACK_UP("\u25B2"),

    /** Value: '\u25B3' */
    TRIANGLE_WHITE_UP("\u25B3"),
    /** Value: '\u25B6' */
    TRIANGLE_BLACK_RIGHT("\u25B6"),
    /** Value: '\u25B7' */
    TRIANGLE_WHITE_RIGHT("\u25B7"),
    /** Value: '\u25BC' */
    TRIANGLE_BLACK_DOWN("\u25BC"),
    /** Value: '\u25BD' */
    TRIANGLE_WHITE_DOWN("\u25BD"),
    /** Value: '\u25C0' */
    TRIANGLE_BLACK_LEFT("\u25C0"),
    /** Value: '\u25C1' */
    TRIANGLE_WHITE_LEFT("\u25C1"),
    /** Value: '\u2726' */
    STAR_4("\u2726"),

    /** Value: '\u2727' */
    STAR_WHITE_4("\u2727"),
    /** Value: '\u2736' */
    STAR_6("\u2736"),
    /** Value: '\u2738' */
    STAR_8("\u2738"),
    /** Value: '\u279F' */
    ARROW_TRIPLE_LINE("\u279F"),

    /** Value: '\u279C' */
    ARROW_ROUND("\u279C"),
    /** Value: '\u27B2' */
    ARROW_CIRCLE("\u27B2"),
    /** Value: '\u2190' */
    ARROW_SMALL_LEFT("\u2190"),

    /** Value: '\u2192' */
    ARROW_SMALL_RIGHT("\u2192"),
    /** Value: '\u21E6' */
    ARROW_WHITE_LEFT("\u21E6"),
    /** Value: '\u21E8' */
    ARROW_WHITE_RIGHT("\u21E8"),
    /** Value: '\u276C' */
    ANGLE_BRACKET_LEFT_LIGHT("\u276C"),

    /** Value: '\u276D' */
    ANGLE_BRACKET_RIGHT_LIGHT("\u276D"),
    /** Value: '\u276E' */
    ANGLE_BRACKET_LEFT_MEDIUM("\u276E"),
    /** Value: '\u276F' */
    ANGLE_BRACKET_RIGHT_MEDIUM("\u276F"),
    /** Value: '\u2770' */
    ANGLE_BRACKET_LEFT_HEAVY("\u2770"),
    /** Value: '\u2771' */
    ANGLE_BRACKET_RIGHT_HEAVY("\u2771");

    private String character;

    Unicode(String character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return character;
    }

    // <editor-fold desc="Documentation Writer">
    // DOCUMENTATION WRITER
    // public static void main(String[] args) {
    // for (Unicode unicode : Unicode.values()) {
    // String escaped =
    // org.apache.commons.lang.StringEscapeUtils.escapeJava(unicode.character);
    // System.out.println("/**");
    //
    // System.out.print(" * ");
    // System.out.print("Value: '");
    // System.out.print(escaped);
    // System.out.print("'");
    // System.out.println();
    //
    // System.out.println(" */");
    // System.out.println(unicode.name() + "(\"" + escaped + "\"),");
    // if (unicode == CROSS
    // || unicode == PENCIL_LOWER_RIGHT
    // || unicode == COIN
    // || unicode == CIRCLE_SMALL
    // || unicode == BLOCK_THIN
    // || unicode == DOUBLE_ANGLE_RIGHT
    // || unicode == TRIANGLE_BLACK_UP
    // || unicode == STAR_4
    // || unicode == ARROW_TRIPLE_LINE
    // || unicode == ARROW_SMALL_LEFT
    // || unicode == ANGLE_BRACKET_LEFT_LIGHT
    // ) {
    // System.out.println();
    // }
    // }
    // }
    // </editor-fold>
}
