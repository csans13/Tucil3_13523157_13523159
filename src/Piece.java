public class Piece {
    private char id;
    private int row;
    private int col;
    private int length;
    private boolean isHorizontal;
    private boolean isPrimary;

    public Piece(char id, int row, int col, int length, boolean isHorizontal, boolean isPrimary) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.length = length;
        this.isHorizontal = isHorizontal;
        this.isPrimary = isPrimary;
    }

    public Piece copy() {
        return new Piece(id, row, col, length, isHorizontal, isPrimary);
    }

    public char getId() {
        return id;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getLength() {
        return length;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public int getEndRow() {
        if (isHorizontal) {
            return row;
        } else {
            return row + length - 1;
        }
    }

    public int getEndCol() {
        if (isHorizontal) {
            return col + length - 1;
        } else {
            return col;
        }
    }

    public void move(int amount) {
        if (isHorizontal) {
            col += amount;
        } else {
            row += amount;
        }
    }

    public boolean occupies(int r, int c) {
        if (isHorizontal) {
            return r == row && c >= col && c <= col + length - 1;
        } else {
            return c == col && r >= row && r <= row + length - 1;
        }
    }

    @Override
    public String toString() {
        return String.format("Piece %c at (%d,%d), length=%d, %s, %s", 
                id, row, col, length, 
                isHorizontal ? "horizontal" : "vertical",
                isPrimary ? "primary" : "non-primary");
    }
}
