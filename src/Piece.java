import java.util.Objects;

public class Piece {
    private char label;
    private char orientation; // 'H' or 'V'
    private int x, y;
    private int length;

    public Piece(char label, char orientation, int x, int y, int length) {
        if (orientation != 'H' && orientation != 'V') {
            throw new IllegalArgumentException("Invalid orientation: " + orientation);
        }
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive.");
        }

        this.label = label;
        this.orientation = orientation;
        this.x = x;
        this.y = y;
        this.length = length;
    }

    public char getLabel() {
        return label;
    }

    public char getOrientation() {
        return orientation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLength() {
        return length;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getHash() {
        return Objects.hash(label, orientation, x, y, length);
    }

    public void movePlus() {
        if (orientation == 'H') {
            y += 1;
        } else {
            x += 1;
        }
    }

    public void moveMinus() {
        if (orientation == 'H') {
            y -= 1;
        } else {
            x -= 1;
        }
    }

    public Piece copy() {
        return new Piece(label, orientation, x, y, length);
    }
}