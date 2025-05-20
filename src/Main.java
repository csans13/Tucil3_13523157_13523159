import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueProgram = true;

        Board initialBoard = null;

        while (continueProgram) {
            try {
                if (initialBoard == null) {
                    System.out.println("Please enter the input file path:");
                    System.out.print("➤ ");
                    String filePath = scanner.nextLine();

                    if (filePath.trim().isEmpty()) {
                        System.out.println("✗ Error: File path cannot be empty. Please try again.");
                        continue;
                    }

                    System.out.println("\n✓ Loading puzzle file...");

                    try {
                        long startReadTime = System.currentTimeMillis();
                        initialBoard = readInputFile(filePath);
                        long endReadTime = System.currentTimeMillis();

                        System.out.println("✓ File loaded successfully in " +
                                (endReadTime - startReadTime) + "ms!");
                    } catch (IOException e) {
                        System.out.println("✗ Error loading file: " + e.getMessage());

                        boolean validTryAgainChoice = false;
                        while (!validTryAgainChoice) {
                            System.out.println("Do you want to try another file? (y/n)");
                            System.out.print("➤ ");
                            String tryAgain = scanner.nextLine();

                            if (tryAgain.equalsIgnoreCase("y")) {
                                validTryAgainChoice = true;
                            } else if (tryAgain.equalsIgnoreCase("n")) {
                                validTryAgainChoice = true;
                                continueProgram = false;
                            } else {
                                System.out.println("✗ Invalid input. Please enter 'y' or 'n'.");
                            }
                        }
                        continue;
                    }
                }

                System.out.println("\n" + "⬢ Initial Board:");
                printBoardWithFrame(initialBoard);

                boolean validAlgorithmChoice = false;
                int algorithmChoice = 0;

                while (!validAlgorithmChoice) {
                    System.out.println("\n" + "⬢ Select Algorithm:");
                    printAlgorithmMenu();

                    System.out.print("➤ Enter your choice (1-4): ");
                    String algorithmInput = scanner.nextLine();

                    try {
                        algorithmChoice = Integer.parseInt(algorithmInput);
                        if (algorithmChoice >= 1 && algorithmChoice <= 4) {
                            validAlgorithmChoice = true;
                        } else {
                            System.out.println("✗ Invalid input. Please enter a number between 1 and 4.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("✗ Invalid input. Please enter a valid number.");
                    }
                }

                int heuristicChoice = 1;
                if (algorithmChoice != 2) {
                    boolean validHeuristicChoice = false;

                    while (!validHeuristicChoice) {
                        System.out.println("\n" + "⬢ Select Heuristic:");
                        printHeuristicMenu();

                        System.out.print("➤ Enter your choice (1-2): ");
                        String heuristicInput = scanner.nextLine();

                        try {
                            heuristicChoice = Integer.parseInt(heuristicInput);
                            if (heuristicChoice >= 1 && heuristicChoice <= 2) {
                                validHeuristicChoice = true;
                            } else {
                                System.out.println("✗ Invalid input. Please enter either 1 or 2.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("✗ Invalid input. Please enter a valid number.");
                        }
                    }
                }

                Solver solver;
                String algorithmName = "";
                String heuristicName = getHeuristicName(heuristicChoice);

                int beamWidth = 3;
                if (algorithmChoice == 4) {
                    boolean validBeamWidth = false;

                    while (!validBeamWidth) {
                        System.out.println("\n" + "Enter beam width:");
                        System.out.print("➤ ");
                        String beamWidthInput = scanner.nextLine();

                        try {
                            beamWidth = Integer.parseInt(beamWidthInput);
                            if (beamWidth >= 1) {
                                validBeamWidth = true;
                            } else {
                                System.out.println("✗ Beam width must be a positive integer.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("✗ Invalid input. Please enter a valid number.");
                        }
                    }
                }

                switch (algorithmChoice) {
                    case 1:
                        algorithmName = "Greedy Best First Search";
                        solver = new GreedyBestFirstSearch(initialBoard, heuristicChoice);
                        break;
                    case 2:
                        algorithmName = "Uniform Cost Search (UCS)";
                        solver = new UniformCostSearch(initialBoard);
                        break;
                    case 3:
                        algorithmName = "A* Search";
                        solver = new AStarSearch(initialBoard, heuristicChoice);
                        break;
                    case 4:
                        algorithmName = "Beam Search (width=" + beamWidth + ")";
                        solver = new BeamSearch(initialBoard, heuristicChoice, beamWidth);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid algorithm choice");
                }

                System.out.println("\n" + "⬢ Running " + algorithmName +
                        (algorithmChoice != 2 ? " with " + heuristicName + " heuristic" : "") +
                        "...");

                printLoadingAnimation();

                long startTime = System.currentTimeMillis();
                Solution solution = solver.solve();
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;

                System.out.println("\n" + "══════════ RESULTS ══════════");
                System.out.println("⬢ Algorithm: " + algorithmName);

                if (algorithmChoice != 2) {
                    System.out.println("⬢ Heuristic: " + heuristicName);
                }

                System.out.println("⬢ Execution time: " + executionTime + " ms");
                System.out.println("⬢ Nodes visited: " + solver.getNodesVisited());

                if (solution != null) {
                    int moveCount = solution.getMoves().size();
                    System.out.println("\n✓ SOLUTION FOUND! " +
                            "Number of moves: " + moveCount);

                    System.out.println("══════════ SOLUTION STEPS ══════════");

                    solution.printStepsAnimated();

                    boolean validSaveChoice = false;
                    while (!validSaveChoice) {
                        System.out.println("\n" + "⬢ Do you want to save the solution to a file? (y/n)");
                        System.out.print("➤ ");
                        String saveToFile = scanner.nextLine();

                        if (saveToFile.equalsIgnoreCase("y") || saveToFile.equalsIgnoreCase("n")) {
                            validSaveChoice = true;

                            if (saveToFile.equalsIgnoreCase("y")) {
                                boolean validOutputPath = false;
                                while (!validOutputPath) {
                                    System.out.println("Enter output file path:");
                                    System.out.print("➤ ");
                                    String outputPath = scanner.nextLine();

                                    if (outputPath.trim().isEmpty()) {
                                        System.out.println("✗ Output path cannot be empty. Please try again.");
                                    } else {
                                        try {
                                            solution.saveToFile(outputPath);
                                            System.out.println("✓ Solution saved to " + outputPath);
                                            validOutputPath = true;
                                        } catch (IOException e) {
                                            System.out.println("✗ Error saving file: " + e.getMessage() +
                                                    ". Please try another path.");
                                        }
                                    }
                                }
                            }
                        } else {
                            System.out.println("✗ Invalid input. Please enter 'y' or 'n'.");
                        }
                    }
                } else {
                    System.out.println("\n✗ NO SOLUTION FOUND!");
                    System.out.println("The puzzle may be unsolvable or require more resources than available.");
                }

                boolean validContinueChoice = false;
                while (!validContinueChoice) {
                    System.out.println("\n" + "⬢ Do you want to run another test? (y/n)");
                    System.out.print("➤ ");
                    String continueChoice = scanner.nextLine();

                    if (continueChoice.equalsIgnoreCase("y")) {
                        validContinueChoice = true;
                    } else if (continueChoice.equalsIgnoreCase("n")) {
                        validContinueChoice = true;
                        continueProgram = false;
                    } else {
                        System.out.println("✗ Invalid input. Please enter 'y' or 'n'.");
                    }
                }

                if (continueProgram) {
                    if (initialBoard != null) {
                        boolean validReuseChoice = false;
                        while (!validReuseChoice) {
                            System.out.println("\n" + "⬢ Do you want to reuse the same puzzle file? (y/n)");
                            System.out.print("➤ ");
                            String reuseChoice = scanner.nextLine();

                            if (reuseChoice.equalsIgnoreCase("y")) {
                                validReuseChoice = true;

                                System.out.println("✓ Reusing the same puzzle file.");

                                System.out.println("\n" + "⬢ Initial Board:");
                                printBoardWithFrame(initialBoard);
                            } else if (reuseChoice.equalsIgnoreCase("n")) {
                                validReuseChoice = true;
                                initialBoard = null;
                                continue;
                            } else {
                                System.out.println("✗ Invalid input. Please enter 'y' or 'n'.");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("✗ Unexpected error: " + e.getMessage());
                e.printStackTrace();

                boolean validContinueAfterErrorChoice = false;
                while (!validContinueAfterErrorChoice) {
                    System.out.println("Do you want to continue? (y/n)");
                    System.out.print("➤ ");
                    String continueAfterError = scanner.nextLine();

                    if (continueAfterError.equalsIgnoreCase("y")) {
                        validContinueAfterErrorChoice = true;
                    } else if (continueAfterError.equalsIgnoreCase("n")) {
                        validContinueAfterErrorChoice = true;
                        continueProgram = false;
                    } else {
                        System.out.println("✗ Invalid input. Please enter 'y' or 'n'.");
                    }
                }
            }
        }

        System.out.println("\n" + "══════════ THANK YOU FOR USING RUSH HOUR SOLVER! ══════════");
        scanner.close();
    }

    private static void printAlgorithmMenu() {
        System.out.println("╔════╦═══════════════════════════════════════╗");
        System.out.println("║" + " 1  " + "║" + " Greedy Best First Search              " + "║");
        System.out.println("╠════╬═══════════════════════════════════════╣");
        System.out.println("║" + " 2  " + "║" + " Uniform Cost Search (UCS)             " + "║");
        System.out.println("╠════╬═══════════════════════════════════════╣");
        System.out.println("║" + " 3  " + "║" + " A* Search                             " + "║");
        System.out.println("╠════╬═══════════════════════════════════════╣");
        System.out.println("║" + " 4  " + "║" + " Beam Search (Bonus)                   " + "║");
        System.out.println("╚════╩═══════════════════════════════════════╝");
    }

    private static void printHeuristicMenu() {
        System.out.println("╔════╦═══════════════════════════════════════╗");
        System.out.println("║" + " 1  " + "║" + " Manhattan Distance to Exit            " + "║");
        System.out.println("╠════╬═══════════════════════════════════════╣");
        System.out.println("║" + " 2  " + "║" + " Blocking Vehicles Count               " + "║");
        System.out.println("╚════╩═══════════════════════════════════════╝");
    }

    private static void printLoadingAnimation() {
        String[] frames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        try {
            for (int i = 0; i < 20; i++) {
                System.out.print("\r" + frames[i % frames.length] +
                        " Solving puzzle... " + (i * 5) + "%");
                Thread.sleep(100);
            }
            System.out.println("\r" + "✓ Puzzle solved!                 ");
        } catch (InterruptedException e) {}
    }

    private static void printBoardWithFrame(Board board) {
        char[][] grid = board.getGrid();
        int rows = board.getRows();
        int cols = board.getCols();

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
        boolean isSolved = board.isSolved();
        
        if (exitSide == 'T' || exitSide == 'B') {
            externalKCol = primaryCol;
        } else if (exitSide == 'L' || exitSide == 'R') {
            externalKRow = primaryRow;
        }

        if (exitSide == 'T') {
            System.out.print(" ");
            
            for (int j = 0; j < primaryCol; j++) {
                System.out.print(" ");
            }
            
            if (isSolved) {
                System.out.println("K " + "PP (Berhasil Keluar)");
            } else {
                System.out.println("K");
            }
        }

        if (exitSide == 'L') {
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
                    System.out.print("K " + "PP ");
                } else {
                    System.out.print("K");
                }
            } else {
                if (exitSide == 'L') {
                    System.out.print(" ");
                }
            }
            
            System.out.print("║");

            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if (cell == 'P') {
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
                System.out.print("(Berhasil Keluar)");
            }
            
            System.out.println();
        }

        if (exitSide == 'L') {
            System.out.print(" " + "╚");
        } else {
            System.out.print("╚");
        }

        for (int j = 0; j < cols; j++) {
            System.out.print("═");
        }
        System.out.println("╝");

        if (exitSide == 'B') {
            System.out.print(" ");
            
            for (int j = 0; j < primaryCol; j++) {
                System.out.print(" ");
            }
            
            if (isSolved) {
                System.out.print("K " + "PP (Berhasil Keluar)");
            } else {
                System.out.print("K");
            }
            System.out.println();
        }

        System.out.println("■ " + "Primary Vehicle (P)  " +
                "■ " + "Exit (K)  " +
                "■ " + "Other Vehicles");
    }

    private static Board readInputFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String dimensionLine = reader.readLine();
            if (dimensionLine == null) {
                throw new IOException("File is empty");
            }

            String[] dimensions = dimensionLine.trim().split("\\s+");
            if (dimensions.length != 2) {
                throw new IOException("Invalid dimension format. Expected two numbers separated by space.");
            }

            int rows, cols;
            try {
                rows = Integer.parseInt(dimensions[0]);
                cols = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid dimension values. Must be integers.");
            }

            if (rows <= 0 || cols <= 0) {
                throw new IOException("Invalid board dimensions: must be positive");
            }

            String countLine = reader.readLine();
            if (countLine == null) {
                throw new IOException("Unexpected end of file after dimensions");
            }

            int numNonPrimaryVehicles;
            try {
                numNonPrimaryVehicles = Integer.parseInt(countLine.trim());
            } catch (NumberFormatException e) {
                throw new IOException("Invalid number of non-primary vehicles. Must be an integer.");
            }

            if (numNonPrimaryVehicles < 0) {
                throw new IOException("Number of non-primary vehicles cannot be negative");
            }

            char[][] grid = new char[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    grid[i][j] = '.';
                }
            }

            List<String> originalLines = new ArrayList<>();

            int externalKRow = -1;
            int externalKCol = -1;
            char exitSide = 'R';

            String line = reader.readLine();
            if (line == null) throw new IOException("Unexpected end of file when reading grid");
            originalLines.add(line);
            
            if (line.trim().indexOf('K') >= 0 && line.trim().length() <= cols) {
                exitSide = 'T';
                externalKCol = line.indexOf('K');
                
                line = reader.readLine();
                if (line == null) throw new IOException("Unexpected end of file after top K row");
                originalLines.add(line);
            }
            
            for (int rowIdx = 0; rowIdx < rows; rowIdx++) {
                String currentLine = (rowIdx == 0) ? line : reader.readLine();
                if (currentLine == null && rowIdx < rows - 1) {
                    throw new IOException("Unexpected end of file at row " + (rowIdx + 1));
                } else if (currentLine.length() < cols) {
                    throw new IOException("Error: Board must be exactly " + (rows) + "x" + (cols));
                }
                
                if (rowIdx > 0) {
                    originalLines.add(currentLine);
                }
                
                int kIndex = currentLine.indexOf('K');
                
                if (kIndex == 0) {
                    exitSide = 'L';
                    externalKRow = rowIdx;
                    
                    if (currentLine.length() > 1) {
                        fillGridRow(grid, rowIdx, currentLine.substring(1));
                    }
                } 
                else if (kIndex > 0 && kIndex < cols) {
                    throw new IOException("The exit (K) was found inside the grid");
                }
                else if (kIndex >= cols) {
                    exitSide = 'R';
                    externalKRow = rowIdx;
                    
                    fillGridRow(grid, rowIdx, currentLine);
                }
                else {
                    fillGridRow(grid, rowIdx, currentLine);
                }
            }
            
            String bottomLine = reader.readLine();
            if (bottomLine != null && bottomLine.trim().indexOf('K') >= 0) {
                originalLines.add(bottomLine);
                exitSide = 'B';
                externalKCol = bottomLine.indexOf('K');
            } else if (bottomLine != null) {
                throw new IOException("Error: Board must be exactly " + (rows) + "x" + (cols));
            }
            
            int primaryPieceRow = -1;
            int primaryPieceCol = -1;
            boolean primaryIsHorizontal = false;
            
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (grid[i][j] == 'P') {
                        if (primaryPieceRow == -1) {
                            primaryPieceRow = i;
                            primaryPieceCol = j;
                            
                            if (j + 1 < cols && grid[i][j + 1] == 'P') {
                                primaryIsHorizontal = true;
                            }
                        }
                    }
                }
            }
            
            if (primaryPieceRow >= 0 && primaryPieceCol >= 0) {
                if ((exitSide == 'L' || exitSide == 'R') && !primaryIsHorizontal) {
                    throw new IOException("Exit orientation (horizontal) doesn't match primary piece orientation (vertical)");
                } else if ((exitSide == 'T' || exitSide == 'B') && primaryIsHorizontal) {
                    throw new IOException("Exit orientation (vertical) doesn't match primary piece orientation (horizontal)");
                }
            }
            
            boolean hasPrimary = primaryPieceRow >= 0;
            if (!hasPrimary) {
                throw new IOException("No primary vehicle (P) found on the board");
            }
            
            if (externalKRow == -1 && externalKCol == -1) {
                throw new IOException("No exit (K) found outside the grid or at any edge");
            }
            
            int actualVehicleCount = 0;
            boolean[] vehicleFound = new boolean[128];
            
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    char c = grid[i][j];
                    if (c != '.' && c != 'P' && c != 'K' && !vehicleFound[c]) {
                        vehicleFound[c] = true;
                        actualVehicleCount++;
                    }
                }
            }
            
            if (actualVehicleCount != numNonPrimaryVehicles) {
                System.out.println("Warning: Number of non-primary vehicles in file (" +
                        numNonPrimaryVehicles + ") doesn't match actual count (" +
                        actualVehicleCount + ")");
            }
            
            Board board = new Board(grid, rows, cols);
            
            board.setExternalKRow(externalKRow);
            board.setExternalKCol(externalKCol);
            board.setExitSide(exitSide);
            
            board.setOriginalLines(originalLines);
            
            return board;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Error reading file: " + e.getMessage(), e);
        }
    }

    private static void fillGridRow(char[][] grid, int rowIdx, String line) {
        int cols = grid[0].length;
        int gridColIdx = 0;
        
        for (int j = 0; j < cols; j++) {
            grid[rowIdx][j] = '.';
        }
        
        for (int j = 0; j < line.length(); j++) {
            char c = line.charAt(j);
            if (c == ' ') {
                continue;
            } else if (gridColIdx < cols) {
                grid[rowIdx][gridColIdx] = c;
                gridColIdx++;
            }
        }
    }

    private static String getHeuristicName(int choice) {
        switch (choice) {
            case 1:
                return "Manhattan Distance to Exit";
            case 2:
                return "Blocking Vehicles Count";
            default:
                return "Unknown";
        }
    }
}