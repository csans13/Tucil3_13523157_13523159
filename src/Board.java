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
   private Piece primaryPiece;
   
   // Buat debugging
   private static final boolean DEBUG = false;
   

   public Board(char[][] grid, int rows, int cols) {
       this.grid = new char[rows][cols];
       this.rows = rows;
       this.cols = cols;
       
       for (int i = 0; i < rows; i++) {
           for (int j = 0; j < cols; j++) {
               this.grid[i][j] = grid[i][j];
           }
       }
       
       pieces = new ArrayList<>();
       
       findExit();
       
       extractPieces();
   }
   
   private void log(String message) {
       if (DEBUG) {
           System.out.println(message);
       }
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
       
       if (pieces.size() != newBoard.pieces.size()) {
           throw new IllegalStateException("Piece list size mismatch after copy: original=" + pieces.size() + ", copy=" + newBoard.pieces.size());
       }
       
       return newBoard;
   }
   
   // Pastikan exit ada di grid
   private void ensureExitIsMarked() {
       if (exitRow >= 0 && exitRow < rows && exitCol >= 0 && exitCol < cols) {
           grid[exitRow][exitCol] = 'K';
           
           log("Exit position ensured at (" + exitRow + "," + exitCol + ")");
       }
   }
   
   // Cari posisi exit di grid
   private void findExit() {
       boolean found = false;
       for (int i = 0; i < rows; i++) {
           for (int j = 0; j < cols; j++) {
               if (grid[i][j] == 'K') {
                   exitRow = i;
                   exitCol = j;
                   found = true;
                   log("Exit found at position (" + exitRow + "," + exitCol + ")");
                   break;
               }
           }
           if (found) break;
       }
       
       if (!found) {
           exitRow = rows / 2;
           exitCol = cols - 1;
           log("Default exit position set to (" + exitRow + "," + exitCol + ")");
       }
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
                       exitRow = startRow;
                       exitCol = cols - 1;
                   } else {
                       exitRow = rows - 1;
                       exitCol = startCol;
                   }
                   log("Exit position set based on primary piece: (" + exitRow + "," + exitCol + ")");
               }
           }
       }
       
       if (primaryPiece == null) {
           throw new IllegalArgumentException("No primary piece (P) found on the board");
       }
   }
   
   // Cek apakah PP sudah sampai di exit
   public boolean isSolved() {
       if (primaryPiece.isHorizontal()) {
           int endCol = primaryPiece.getCol() + primaryPiece.getLength() - 1;
           
           if (primaryPiece.getRow() != exitRow) {
               return false;
           }
           
           if (exitCol == cols - 1) {
               return endCol == cols - 1;
           } else if (exitCol == 0) {
               return primaryPiece.getCol() == 0;
           }
       } else {
           int endRow = primaryPiece.getRow() + primaryPiece.getLength() - 1;
           
           if (primaryPiece.getCol() != exitCol) {
               return false;
           }
           
           if (exitRow == rows - 1) {
               return endRow == rows - 1;
           } else if (exitRow == 0) {
               return primaryPiece.getRow() == 0;
           }
       }
       
       return false;
   }
   
    // Cari semua gerakan yang bisa dilakukan
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
   
    // Terapkan gerakan pada board
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
                   if (r == newBoard.exitRow && c == newBoard.exitCol) {
                       newBoard.grid[r][c] = 'K';
                   } else {
                       newBoard.grid[r][c] = '.';
                   }
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
                   if (r == newBoard.exitRow && c == newBoard.exitCol) {
                       continue;
                   }
                   newBoard.grid[r][c] = pieceToMove.getId();
               }
           }
       }
       
       if (pieceToMove.isPrimary()) {
           newBoard.primaryPiece = pieceToMove;
       }
       
       newBoard.ensureExitIsMarked();
       
       return newBoard;
   }

    //  Cetak board
   public void print() {
       String reset = "\u001B[0m";
       String redColor = "\u001B[31m";
       String greenColor = "\u001B[32m";
       
       for (int i = 0; i < rows; i++) {
           for (int j = 0; j < cols; j++) {
               char cell = grid[i][j];
               if (cell == 'P') {
                   System.out.print(redColor + cell + reset);
               } else if (cell == 'K') {
                   System.out.print(greenColor + cell + reset);
               } else {
                   System.out.print(cell);
               }
           }
           System.out.println();
       }
   }
   
   // Heuristik 1: jarak Manhattan ke exit
   public int manhattanDistanceToExit() {
       if (primaryPiece.isHorizontal()) {
           if (exitCol > primaryPiece.getEndCol()) {
               return exitCol - primaryPiece.getEndCol();
           } else {
               return primaryPiece.getCol() - exitCol;
           }
       } else {
           if (exitRow > primaryPiece.getEndRow()) {
               return exitRow - primaryPiece.getEndRow();
           } else {
               return primaryPiece.getRow() - exitRow;
           }
       }
   }
   
   // Heuristik 2: blocking pieces 
   public int blockingPiecesCount() {
       int count = 0;
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
       } else {
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
       
       return count;
   }
   
   // Opsi untuk memilih heuristik
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
   
   public int getRows() {
       return rows;
   }
   
   public int getCols() {
       return cols;
   }
   
   public List<Piece> getPieces() {
       return pieces;
   }
   
   public char[][] getGrid() {
       return grid;
   }
   
   public int getExitRow() {
       return exitRow;
   }
   
   public int getExitCol() {
       return exitCol;
   }
   
   public Piece getPrimaryPiece() {
       return primaryPiece;
   }
   
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