package util;

public enum Emoji {
    TOILET('\uD83D', '\uDEBD'),
    BACK_WITH_LEFTWARDS_ARROW_ABOVE('\uD83D', '\uDD19'),
    FILE_FOLDER('\uD83D', '\uDCC1'),
    SUSHI('\uD83C', '\uDF63'),
    CASH('\uD83D', '\uDCB0');

    Character firstChar;
    Character secondChar;

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