import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Piece> pieces;
    private long hash;
    private int rows, cols;
    private char exit;

    public Board(int rows, int cols, char exit) {
        this.pieces = new ArrayList<>();
        this.rows = rows;
        this.cols = cols;
        this.exit = exit;
        this.hash = 0;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public char getExit() {
        return exit;
    }

    public List<Piece> getPieces() {
        List<Piece> copyList = new ArrayList<>();
        for (Piece p : pieces) {
            copyList.add(p.copy());
        }
        return copyList;
    }

    public boolean isValidPlacement(Piece p) {
        if (p == null) {
            return false;
        }

        if (p.getOrientation() == 'H') {
            if (p.getY() < 0 || p.getY() + p.getLength() > cols) {
                return false;
            }
            if (p.getX() < 0 || p.getX() >= rows) {
                return false;
            }
        } else {
            if (p.getX() < 0 || p.getX() + p.getLength() > rows) {
                return false;
            }
            if (p.getY() < 0 || p.getY() >= cols) {
                return false;
            }
        }

        char[][] boardMatrix = getBoard();
        if (p.getOrientation() == 'H') {
            for (int i = 0; i < p.getLength(); i++) {
                if (boardMatrix[p.getX()][p.getY() + 1] != '.') {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < p.getLength(); i++) {
                if (boardMatrix[p.getX() + i][p.getY()] != '.') {
                    return false;
                }
            }
        }

        return true;
    }
    
    public boolean addPiece(Piece p) {
        if (p != null && isValidPlacement(p)) {
            pieces.add(p);
            recalculateHash();
        }
        return false;
    }

    public void resetPieces() {
        pieces.clear();
        hash = 0;
    }

    public void recalculateHash() {
        hash = 0;
        for (Piece p : pieces) {
            hash += p.getHash();
        }
    }

    public char[][] getBoard() {
        char[][] boardMatrix = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boardMatrix[i][j] = '.';
            }
        }

        for (Piece p : pieces) {
            char label = p.getLabel();
            int x = p.getX();
            int y = p.getY();
            int length = p.getLength();
            char orientation = p.getOrientation();

            if (orientation == 'H') {
                for (int i = 0; i < length; i++) {
                    boardMatrix[x][y + i] = label;
                }
            } else {
                for (int i = 0; i < length; i++) {
                    boardMatrix[x + i][y] = label;
                }
            }
        }
        
        return boardMatrix;
    }

    public void print() {
        char[][] boardMatrix = getBoard();
        for (char[] row : boardMatrix) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public long getHash() {
        return hash;
    }
}