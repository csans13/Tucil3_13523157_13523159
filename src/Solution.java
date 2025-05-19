import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Solution {
    private List<Node> path;

    public Solution(List<Node> path) {
        this.path = path;
    }

    public List<Node> getMoves() {
        return path.subList(1, path.size()); 
    }

    public void printSteps() {
        System.out.println("Papan Awal:");
        printBoardWithFrame(path.get(0).getBoard(), -1);

        for (int i = 1; i < path.size(); i++) {
            Node move = path.get(i);
            Piece piece = move.getBoard().getPieces().get(move.getPieceIdx());

            String direction;
            if (piece.isHorizontal()) {
                direction = move.getMoveAmount() > 0 ? "right" : "left";
            } else {
                direction = move.getMoveAmount() > 0 ? "down" : "up";
            }

            System.out.println("Gerakan " + i + ": " + piece.getId() + "-" + direction);
            System.out.println(move.getBoard().toString());
        }
    }

    public void printStepsAnimated() {
        System.out.println("Papan Awal");
        printBoardWithFrame(path.get(0).getBoard(), -1);

        try {
            for (int i = 1; i < path.size(); i++) {
                Node move = path.get(i);
                Piece piece = move.getBoard().getPieces().get(move.getPieceIdx());

                String direction;
                if (piece.isHorizontal()) {
                    direction = move.getMoveAmount() > 0 ? "right" : "left";
                } else {
                    direction = move.getMoveAmount() > 0 ? "down" : "up";
                }

                Thread.sleep(800);

                System.out.println("Gerakan " + i + ": " + piece.getId() + "-" + direction);
                printBoardWithFrame(move.getBoard(), move.getPieceIdx());
            }
        } catch (InterruptedException e) {}
    }

    private void printBoardWithFrame(Board board, int movedPieceIdx) {
        char[][] grid = board.getGrid();
        int rows = board.getRows();
        int cols = board.getCols();

        char movedPieceId = (movedPieceIdx >= 0) ?
                board.getPieces().get(movedPieceIdx).getId() : ' ';

        Piece primaryPiece = null;
        for (Piece piece : board.getPieces()) {
            if (piece.isPrimary()) {
                primaryPiece = piece;
                break;
            }
        }
        
        if (primaryPiece == null) {
            throw new IllegalStateException("Primary piece not found");
        }

        int primaryRow = primaryPiece.getRow();
        int primaryCol = primaryPiece.getCol();
        boolean isHorizontal = primaryPiece.isHorizontal();
        
        int externalKRow = board.getExternalKRow();
        int externalKCol = board.getExternalKCol();
        char exitSide = board.getExitSide();
        
        if (exitSide == 'T' || exitSide == 'B') {
            externalKCol = primaryCol;
        } else if (exitSide == 'L' || exitSide == 'R') {
            externalKRow = primaryRow;
        }
        
        boolean isSolved = board.isSolved();

        if (exitSide == 'T') {
            if (isSolved) {
                for (int index = 0; index < 4; index++) {
                    System.out.print(" ");
                    
                    for (int j = 0; j < primaryCol; j++) {
                        System.out.print(" ");
                    }
                    
                    if (index == 0) {
                        System.out.println("P (Berhasil Keluar)");
                    } else if (index == 1) {
                        System.out.println("P");
                    } else if (index == 2) {
                        System.out.println();
                    } else if (index == 3) {
                        System.out.println("K");                        
                    }
                }
            } else {
                System.out.print(" ");
                
                for (int j = 0; j < primaryCol; j++) {
                    System.out.print(" ");
                }
                
                System.out.println("K");
            }
        }

        if (exitSide == 'L' && isSolved) {
            System.out.print("    " + "╔");
        } else if (exitSide == 'L') {
            System.out.print(" " + "╔");
        } else {
            System.out.print("╔");
        }
        
        for (int j = 0; j < cols; j++) {
            System.out.print("═");
        }
        System.out.println("╗");

        for (int i = 0; i < rows; i++) {
            if (exitSide == 'L' && i == primaryRow) {
                if (isSolved) {
                    System.out.print("PP " + "K");
                } else {
                    System.out.print("K");
                }
            } else {
                if (exitSide == 'L' && isSolved) {
                    System.out.print("    ");
                } else if (exitSide == 'L') {
                    System.out.print(" ");
                }
            }
            
            System.out.print("║");

            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if ((isSolved && exitSide == 'R' && i == externalKRow && cell == 'P') ||
                    (isSolved && exitSide == 'L' && i == externalKRow && cell == 'P') ||
                    (isSolved && exitSide == 'T' && j == externalKCol && cell == 'P') ||
                    (isSolved && exitSide == 'B' && j == externalKCol && cell == 'P')) {
                    System.out.print("·");
                } else if (cell == 'P') {
                    System.out.print("P");
                } else if (cell == movedPieceId) {
                    System.out.print(cell);
                } else if (cell == '.') {
                    System.out.print("·");
                } else if (cell == 'K') {
                    System.out.print("·");
                } else {
                    System.out.print(cell);
                }
            }
            
            if (exitSide == 'R' && i == primaryRow) {
                System.out.print("║" + "K");
                if (isSolved) {
                    System.out.print(" " + "PP (Berhasil Keluar)");
                }
            } else {
                System.out.print("║");
            }
            
            if (exitSide == 'L' && i == primaryRow && isSolved) {
                System.out.print(" (Berhasil Keluar)");
            }
            
            System.out.println();
        }

        if (exitSide == 'L' && isSolved) {
            System.out.print("    " + "╚");
        } else if (exitSide == 'L') {
            System.out.print(" " + "╚");
        } else {
            System.out.print("╚");
        }

        for (int j = 0; j < cols; j++) {
            System.out.print("═");
        }
        System.out.println("╝");

        if (exitSide == 'B') {
            if (isSolved) {
                for (int index = 0; index < 4; index++) {
                    System.out.print(" ");
                    
                    for (int j = 0; j < primaryCol; j++) {
                        System.out.print(" ");
                    }
                    
                    if (index == 0) {
                        System.out.println("K");
                    } else if (index == 1) {
                        System.out.println();                        
                    } else if (index == 2) {
                        System.out.println("P");
                    } else if (index == 3) {
                        System.out.println("P (Berhasil Keluar)");
                    }
                }
            } else {
                System.out.print(" ");
                
                for (int j = 0; j < primaryCol; j++) {
                    System.out.print(" ");
                }
                
                System.out.println("K");
            }
        }

        if (movedPieceIdx >= 0) {
            System.out.println("■ " + "Primary Piece (P)  " +
                    "■ " + "Exit (K)  " +
                    "■ " + "Moved Piece (" + movedPieceId + ")");
        } else {
            System.out.println("■ " + "Primary Piece (P)  " +
                    "■ " + "Exit (K)  " +
                    "■ " + "Other Pieces");
        }
    }

    public void saveToFile(String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);

        writer.write("Papan Awal\n");
        writer.write(path.get(0).getBoard().toString());
        writer.write("\n");

        for (int i = 1; i < path.size(); i++) {
            Node move = path.get(i);

            String direction;
            Piece piece = move.getBoard().getPieces().get(move.getPieceIdx());

            if (piece.isHorizontal()) {
                direction = move.getMoveAmount() > 0 ? "right" : "left";
            } else {
                direction = move.getMoveAmount() > 0 ? "down" : "up";
            }

            writer.write("Gerakan " + i + ": " + piece.getId() + "-" + direction + "\n");
            writer.write(move.getBoard().toString());
            writer.write("\n");
        }

        writer.close();
    }
}