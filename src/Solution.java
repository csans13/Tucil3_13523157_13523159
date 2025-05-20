import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Solution {
    private static final String RESET = Main.RESET;
    private static final String BRIGHT_RED = Main.BRIGHT_RED;
    private static final String BRIGHT_GREEN = Main.BRIGHT_GREEN;
    private static final String BRIGHT_YELLOW = Main.BRIGHT_YELLOW;
    private static final String BRIGHT_BLUE = Main.BRIGHT_BLUE;
    private static final String BRIGHT_CYAN = Main.BRIGHT_CYAN;
    private static final String BRIGHT_WHITE = Main.BRIGHT_WHITE;
    private static final String BOLD = Main.BOLD;

    private List<Node> path;

    public Solution(List<Node> path) {
        this.path = path;
    }

    public List<Node> getMoves() {
        return path.subList(1, path.size());
    }

    public void printSteps() {
        System.out.println(BOLD + BRIGHT_CYAN + "Papan Awal" + RESET);
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

            System.out.println("\n" + BOLD + BRIGHT_CYAN + "Gerakan " + i + ": " +
                    BRIGHT_YELLOW + piece.getId() + "-" + direction + RESET);

            printBoardWithFrame(move.getBoard(), move.getPieceIdx());
        }
    }

    public void printStepsAnimated() {
        System.out.println(BOLD + BRIGHT_CYAN + "Papan Awal" + RESET);
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

                System.out.println("\n" + BOLD + BRIGHT_CYAN + "Gerakan " + i + ": " +
                        BRIGHT_YELLOW + piece.getId() + "-" + direction + RESET);

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
                        System.out.println(BRIGHT_RED + "P (Berhasil Keluar)" + RESET);
                    } else if (index == 1) {
                        System.out.println(BRIGHT_RED + "P" + RESET);
                    } else if (index == 2) {
                        System.out.println();
                    } else if (index == 3) {
                        System.out.println(BRIGHT_GREEN + "K" + RESET);                        
                    }
                }
            } else {
                System.out.print(" ");
                
                for (int j = 0; j < primaryCol; j++) {
                    System.out.print(" ");
                }
                
                System.out.println(BRIGHT_GREEN + "K" + RESET);
            }
        }

        if (exitSide == 'L' && isSolved) {
            System.out.print("    " + BRIGHT_CYAN + "╔");
        } else if (exitSide == 'L') {
            System.out.print(" " + BRIGHT_CYAN + "╔");
        } else {
            System.out.print(BRIGHT_CYAN + "╔");
        }
        
        for (int j = 0; j < cols; j++) {
            System.out.print("═");
        }
        System.out.println("╗" + RESET);

        for (int i = 0; i < rows; i++) {
            if (exitSide == 'L' && i == primaryRow) {
                if (isSolved) {
                    System.out.print(BRIGHT_RED + "PP " + BRIGHT_GREEN + "K" + RESET);
                } else {
                    System.out.print(BRIGHT_GREEN + "K" + RESET);
                }
            } else {
                if (exitSide == 'L' && isSolved) {
                    System.out.print("    ");
                } else if (exitSide == 'L') {
                    System.out.print(" ");
                }
            }
            
            System.out.print(BRIGHT_CYAN + "║" + RESET);

            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if ((isSolved && exitSide == 'R' && i == externalKRow && cell == 'P') ||
                    (isSolved && exitSide == 'L' && i == externalKRow && cell == 'P') ||
                    (isSolved && exitSide == 'T' && j == externalKCol && cell == 'P') ||
                    (isSolved && exitSide == 'B' && j == externalKCol && cell == 'P')) {
                    System.out.print(BRIGHT_WHITE + "·" + RESET);
                } else if (cell == 'P') {
                    System.out.print(BRIGHT_RED + "P" + RESET);
                } else if (cell == movedPieceId) {
                    System.out.print(BRIGHT_YELLOW + cell + RESET);
                } else if (cell == '.') {
                    System.out.print(BRIGHT_WHITE + "·" + RESET);
                } else if (cell == 'K') {
                    System.out.print(BRIGHT_WHITE + "·" + RESET);
                } else {
                    System.out.print(BRIGHT_BLUE + cell + RESET);
                }
            }
            
            if (exitSide == 'R' && i == primaryRow) {
                System.out.print(BRIGHT_CYAN + "║" + RESET + BRIGHT_GREEN + "K " + RESET);
                if (isSolved) {
                    System.out.print(BRIGHT_RED + "PP (Berhasil Keluar)" + RESET);
                }
            } else {
                System.out.print(BRIGHT_CYAN + "║" + RESET);
            }
            
            if (exitSide == 'L' && i == primaryRow && isSolved) {
                System.out.print(BRIGHT_RED + " (Berhasil Keluar)" + RESET);
            }
            
            System.out.println();
        }

        if (exitSide == 'L' && isSolved) {
            System.out.print("    " + BRIGHT_CYAN + "╚");
        } else if (exitSide == 'L') {
            System.out.print(" " + BRIGHT_CYAN + "╚");
        } else {
            System.out.print(BRIGHT_CYAN + "╚");
        }

        for (int j = 0; j < cols; j++) {
            System.out.print("═");
        }
        System.out.println("╝" + RESET);

        if (exitSide == 'B') {
            if (isSolved) {
                for (int index = 0; index < 4; index++) {
                    System.out.print(" ");
                    
                    for (int j = 0; j < primaryCol; j++) {
                        System.out.print(" ");
                    }
                    
                    if (index == 0) {
                        System.out.println(BRIGHT_GREEN + "K" + RESET);
                    } else if (index == 1) {
                        System.out.println();                        
                    } else if (index == 2) {
                        System.out.println(BRIGHT_RED + "P" + RESET);
                    } else if (index == 3) {
                        System.out.println(BRIGHT_RED + "P (Berhasil Keluar)" + RESET);
                    }
                }
            } else {
                System.out.print(" ");
                
                for (int j = 0; j < primaryCol; j++) {
                    System.out.print(" ");
                }
                
                System.out.println(BRIGHT_GREEN + "K" + RESET);
            }
        }

        if (movedPieceIdx >= 0) {
            System.out.println(BRIGHT_RED + "■ " + RESET + "Primary Piece (P)  " +
                    BRIGHT_GREEN + "■ " + RESET + "Exit (K)  " +
                    BRIGHT_YELLOW + "■ " + RESET + "Moved Piece (" + movedPieceId + ")");
        } else {
            System.out.println(BRIGHT_RED + "■ " + RESET + "Primary Piece (P)  " +
                    BRIGHT_GREEN + "■ " + RESET + "Exit (K)  " +
                    BRIGHT_BLUE + "■ " + RESET + "Other Pieces");
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