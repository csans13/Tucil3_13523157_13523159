import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private int rows;
    private int cols;
    private List<Piece> pieces;
    private char[][] grid;
    private int exitRow;
    private int exitCol;
    private char exitSide; // 'T' untuk atas, 'R' untuk kanan, 'B' untuk bawah, 'L' untuk kiri
    private Piece primaryPiece;
    private List<String> originalLines;
    private int externalKRow = -1; 
    private int externalKCol = -1; 

    // Constructor
    public Board(char[][] grid, int rows, int cols) {
        this.grid = new char[rows][cols];
        this.rows = rows;
        this.cols = cols;
        this.originalLines = new ArrayList<>();
        this.externalKRow = -1;
        this.externalKCol = -1;
        this.exitSide = 'R';
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.grid[i][j] = grid[i][j];
            }
        }
        pieces = new ArrayList<>();
        findExit();
        extractPieces();
    }

    // Buat copy dari board
    public Board copy() {
        char[][] newGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newGrid[i][j] = grid[i][j];
            }
        }
        Board newBoard = new Board(newGrid, rows, cols);
        newBoard.pieces = new ArrayList<>();
        for (Piece piece : pieces) {
            newBoard.pieces.add(piece.copy());
        }
        for (Piece piece : newBoard.pieces) {
            if (piece.isPrimary()) {
                newBoard.primaryPiece = piece;
                break;
            }
        }
        newBoard.exitRow = this.exitRow;
        newBoard.exitCol = this.exitCol;
        newBoard.exitSide = this.exitSide;
        newBoard.externalKRow = this.externalKRow;
        newBoard.externalKCol = this.externalKCol;
        newBoard.originalLines = this.originalLines;
        if (pieces.size() != newBoard.pieces.size()) {
            throw new IllegalStateException("Piece list size mismatch after copy: original=" + pieces.size() + ", copy=" + newBoard.pieces.size());
        }
        return newBoard;
    }

    // Cari posisi exit di grid
    private void findExit() {
        if (externalKRow >= 0) {
            exitRow = externalKRow;
            exitCol = cols - 1;
            exitSide = 'R';
            return;
        }
        for (int j = 0; j < cols; j++) {
            if (externalKCol == j) {
                exitRow = 0;
                exitCol = j;
                exitSide = 'T';
                return;
            }
        }
        for (int j = 0; j < cols; j++) {
            if (externalKCol == j) {
                exitRow = rows - 1;
                exitCol = j;
                exitSide = 'B';
                return;
            }
        }
        for (int i = 0; i < rows; i++) {
            if (originalLines != null && originalLines.size() > i) {
                String line = originalLines.get(i);
                if (line.length() > 0 && line.charAt(0) == 'K') {
                    exitRow = i;
                    exitCol = 0;
                    exitSide = 'L';
                    return;
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'P') {
                    boolean isHorizontal = (j + 1 < cols && grid[i][j + 1] == 'P');
                    if (isHorizontal) {
                        exitRow = i;
                        exitCol = cols - 1;
                        exitSide = 'R';
                    } else {
                        exitRow = rows - 1;
                        exitCol = j;
                        exitSide = 'B';
                    }
                    return;
                }
            }
        }
        exitRow = rows / 2;
        exitCol = cols - 1;
        exitSide = 'R';
    }

    // Ambil semua piece dari grid
    private void extractPieces() {
        Map<Character, List<int[]>> piecePositions = new HashMap<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if (cell != '.' && cell != 'K') {
                    piecePositions.computeIfAbsent(cell, k -> new ArrayList<>()).add(new int[]{i, j});
                }
            }
        }
        for (Map.Entry<Character, List<int[]>> entry : piecePositions.entrySet()) {
            char pieceId = entry.getKey();
            List<int[]> positions = entry.getValue();
            positions.sort((a, b) -> {
                int rowDiff = Integer.compare(a[0], b[0]);
                if (rowDiff != 0) return rowDiff;
                return Integer.compare(a[1], b[1]);
            });
            boolean isHorizontal = true;
            if (positions.size() > 1) {
                isHorizontal = positions.get(0)[0] == positions.get(1)[0];
            }
            int startRow = positions.get(0)[0];
            int startCol = positions.get(0)[1];
            int length = positions.size();
            boolean isPrimary = pieceId == 'P';
            Piece piece = new Piece(pieceId, startRow, startCol, length, isHorizontal, isPrimary);
            pieces.add(piece);
            if (isPrimary) {
                primaryPiece = piece;
                if (exitRow < 0 || exitCol < 0) {
                    if (isHorizontal) {
                        if (externalKRow >= 0) {
                            exitRow = externalKRow;
                            exitCol = cols - 1;
                            exitSide = 'R';
                        } else {
                            exitRow = startRow;
                            exitCol = cols - 1;
                            exitSide = 'R';
                        }
                    } else {
                        if (externalKCol >= 0) {
                            if (externalKRow == 0) {
                                exitRow = 0;
                                exitCol = startCol;
                                exitSide = 'T';
                            } else {
                                exitRow = rows - 1;
                                exitCol = startCol;
                                exitSide = 'B';
                            }
                        } else {
                            exitRow = rows - 1;
                            exitCol = startCol;
                            exitSide = 'B';
                        }
                    }
                }
            }
        }
        if (primaryPiece == null) {
            throw new IllegalArgumentException("No primary piece (P) found on the board");
        }
    }

     // Cek apakah PP sudah sampai di exit
    public boolean isSolved() {
        switch (exitSide) {
            case 'R':
                if (primaryPiece.isHorizontal()) {
                    int endCol = primaryPiece.getCol() + primaryPiece.getLength() - 1;
                    return primaryPiece.getRow() == exitRow && endCol >= cols - 1;
                }
                return false;
            case 'L':
                if (primaryPiece.isHorizontal()) {
                    return primaryPiece.getRow() == exitRow && primaryPiece.getCol() <= 0;
                }
                return false;
            case 'T':
                if (!primaryPiece.isHorizontal()) {
                    return primaryPiece.getCol() == exitCol && primaryPiece.getRow() <= 0;
                }
                return false;
            case 'B':
                if (!primaryPiece.isHorizontal()) {
                    int endRow = primaryPiece.getRow() + primaryPiece.getLength() - 1;
                    return primaryPiece.getCol() == exitCol && endRow >= rows - 1;
                }
                return false;
            default:
                return false;
        }
    }

    // Mendapatkan semua possible moves untuk semua pieces
    public List<int[]> getLegalMoves() {
        List<int[]> legalMoves = new ArrayList<>();
        for (int pieceIdx = 0; pieceIdx < pieces.size(); pieceIdx++) {
            Piece piece = pieces.get(pieceIdx);
            if (piece.isHorizontal()) {
                int leftMove = -1;
                while (canMoveHorizontal(piece, leftMove)) {
                    legalMoves.add(new int[]{pieceIdx, leftMove});
                    leftMove--;
                }
                int rightMove = 1;
                while (canMoveHorizontal(piece, rightMove)) {
                    legalMoves.add(new int[]{pieceIdx, rightMove});
                    rightMove++;
                }
            } else {
                int upMove = -1;
                while (canMoveVertical(piece, upMove)) {
                    legalMoves.add(new int[]{pieceIdx, upMove});
                    upMove--;
                }
                int downMove = 1;
                while (canMoveVertical(piece, downMove)) {
                    legalMoves.add(new int[]{pieceIdx, downMove});
                    downMove++;
                }
            }
        }
        return legalMoves;
    }

    // Cek apakah bisa gerak horizontal
    private boolean canMoveHorizontal(Piece piece, int amount) {
        if (!piece.isHorizontal()) return false;
        int newCol = piece.getCol() + amount;
        int endCol = newCol + piece.getLength() - 1;
        if (newCol < 0 || endCol >= cols) return false;
        for (int c = Math.min(piece.getCol(), newCol); c <= Math.max(piece.getEndCol(), endCol); c++) {
            if (c >= piece.getCol() && c <= piece.getEndCol()) continue;
            if (grid[piece.getRow()][c] != '.' && grid[piece.getRow()][c] != 'K') {
                return false;
            }
        }
        return true;
    }

    // Cek apakah bisa gerak vertikal
    private boolean canMoveVertical(Piece piece, int amount) {
        if (piece.isHorizontal()) return false;
        int newRow = piece.getRow() + amount;
        int endRow = newRow + piece.getLength() - 1;
        if (newRow < 0 || endRow >= rows) return false;
        for (int r = Math.min(piece.getRow(), newRow); r <= Math.max(piece.getEndRow(), endRow); r++) {
            if (r >= piece.getRow() && r <= piece.getEndRow()) continue;
            if (grid[r][piece.getCol()] != '.' && grid[r][piece.getCol()] != 'K') {
                return false;
            }
        }
        return true;
    }

    //  Menerapkan moves pada board dan mengembalikan board baru
    public Board applyMove(int pieceIdx, int moveAmount) {
        Board newBoard = this.copy();
        if (pieces.size() != newBoard.pieces.size()) {
            throw new IllegalStateException("Piece list size mismatch after copy: original=" +
                                        pieces.size() + ", copy=" + newBoard.pieces.size());
        }
        Piece pieceToMove = newBoard.pieces.get(pieceIdx);
        int startRow = pieceToMove.getRow();
        int startCol = pieceToMove.getCol();
        int endRow = pieceToMove.getEndRow();
        int endCol = pieceToMove.getEndCol();
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (pieceToMove.occupies(r, c)) {
                    newBoard.grid[r][c] = '.';
                }
            }
        }
        pieceToMove.move(moveAmount);
        startRow = pieceToMove.getRow();
        startCol = pieceToMove.getCol();
        endRow = pieceToMove.getEndRow();
        endCol = pieceToMove.getEndCol();
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                if (pieceToMove.occupies(r, c)) {
                    newBoard.grid[r][c] = pieceToMove.getId();
                }
            }
        }
        if (pieceToMove.isPrimary()) {
            newBoard.primaryPiece = pieceToMove;
        }
        return newBoard;
    }

    // Heuristik 1: jarak Manhattan ke exit
    public int manhattanDistanceToExit() {
        switch (exitSide) {
            case 'R':
                if (primaryPiece.isHorizontal()) {
                    int endCol = primaryPiece.getEndCol();
                    int rowDist = Math.abs(primaryPiece.getRow() - exitRow);
                    int colDist = cols - 1 - endCol;
                    return rowDist + colDist;
                }
                break;
            case 'L':
                if (primaryPiece.isHorizontal()) {
                    int rowDist = Math.abs(primaryPiece.getRow() - exitRow);
                    int colDist = primaryPiece.getCol();
                    return rowDist + colDist;
                }
                break;
            case 'T':
                if (!primaryPiece.isHorizontal()) {
                    int rowDist = primaryPiece.getRow();
                    int colDist = Math.abs(primaryPiece.getCol() - exitCol);
                    return rowDist + colDist;
                }
                break;
            case 'B':
                if (!primaryPiece.isHorizontal()) {
                    int endRow = primaryPiece.getEndRow();
                    int rowDist = rows - 1 - endRow;
                    int colDist = Math.abs(primaryPiece.getCol() - exitCol);
                    return rowDist + colDist;
                }
                break;
        }
        return rows + cols;
    }

    // Heuristik 2: blocking pieces 
    public int blockingPiecesCount() {
        int count = 0;
        switch (exitSide) {
            case 'R':
                if (primaryPiece.isHorizontal()) {
                    int row = primaryPiece.getRow();
                    int startCol = primaryPiece.getEndCol() + 1;
                    int endCol = cols - 1;
                    for (int c = startCol; c <= endCol; c++) {
                        if (grid[row][c] != '.' && grid[row][c] != 'K') {
                            count++;
                            while (c + 1 < cols && grid[row][c] == grid[row][c + 1]) {
                                c++;
                            }
                        }
                    }
                }
                break;
            case 'L':
                if (primaryPiece.isHorizontal()) {
                    int row = primaryPiece.getRow();
                    int startCol = 0;
                    int endCol = primaryPiece.getCol() - 1;
                    for (int c = startCol; c <= endCol; c++) {
                        if (grid[row][c] != '.' && grid[row][c] != 'K') {
                            count++;
                            while (c + 1 <= endCol && grid[row][c] == grid[row][c + 1]) {
                                c++;
                            }
                        }
                    }
                }
                break;
            case 'T':
                if (!primaryPiece.isHorizontal()) {
                    int col = primaryPiece.getCol();
                    int startRow = 0;
                    int endRow = primaryPiece.getRow() - 1;
                    for (int r = startRow; r <= endRow; r++) {
                        if (grid[r][col] != '.' && grid[r][col] != 'K') {
                            count++;
                            while (r + 1 <= endRow && grid[r][col] == grid[r + 1][col]) {
                                r++;
                            }
                        }
                    }
                }
                break;
            case 'B':
                if (!primaryPiece.isHorizontal()) {
                    int col = primaryPiece.getCol();
                    int startRow = primaryPiece.getEndRow() + 1;
                    int endRow = rows - 1;
                    for (int r = startRow; r <= endRow; r++) {
                        if (grid[r][col] != '.' && grid[r][col] != 'K') {
                            count++;
                            while (r + 1 < rows && grid[r][col] == grid[r + 1][col]) {
                                r++;
                            }
                        }
                    }
                }
                break;
        }
        return count;
    }

    // Opsi bagi pengguna untuk memilih heuristik
    public int getHeuristicValue(int heuristicChoice) {
        switch (heuristicChoice) {
            case 1:
                return manhattanDistanceToExit();
            case 2:
                return blockingPiecesCount();
            default:
                return 0;
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public List<Piece> getPieces() { return pieces; }
    public char[][] getGrid() { return grid; }
    public int getExitRow() { return exitRow; }
    public int getExitCol() { return exitCol; }
    public char getExitSide() { return exitSide; }
    public void setExitSide(char side) {
        if (side == 'T' || side == 'R' || side == 'B' || side == 'L') {
            this.exitSide = side;
        }
    }
    public Piece getPrimaryPiece() { return primaryPiece; }
    public void setExternalKRow(int row) { this.externalKRow = row; }
    public int getExternalKRow() { return externalKRow; }
    public void setExternalKCol(int col) { this.externalKCol = col; }
    public int getExternalKCol() { return externalKCol; }
    public void setOriginalLines(List<String> originalLines) { this.originalLines = originalLines; }
    public List<String> getOriginalLines() { return originalLines != null ? originalLines : new ArrayList<>(); }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                hash = 31 * hash + grid[i][j];
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Board other = (Board) obj;
        if (rows != other.rows || cols != other.cols) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != other.grid[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(grid[i][j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}