package util;

public enum Emoji {
    SUBMENU_ITEM('\uD83D', '\uDCC1'),
    MENU_BACK('\uD83D', '\uDD19'),
    ORDER_POSITION('\u2714'),
    ORDER_SUM('\uD83D', '\uDCB0'),
    ORDER_RESET('\u267B');

    Character firstChar;
    Character secondChar;

    Emoji(Character firstChar) {
        this(firstChar, null);
    }

    Emoji(Character firstChar, Character secondChar) {
        this.firstChar = firstChar;
        this.secondChar = secondChar;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (this.firstChar != null) {
            sb.append(this.firstChar);
        }
        if (this.secondChar != null) {
            sb.append(this.secondChar);
        }

        return sb.toString();
    }
}