import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_PURPLE = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";

    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continueProgram = true;

        printWelcomeBanner();

        Board initialBoard = null;

        while (continueProgram) {
            try {
                if (initialBoard == null) {
                    System.out.println(BOLD + BRIGHT_CYAN + "Please enter the input file path:" + RESET);
                    System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                    String filePath = scanner.nextLine();

                    if (filePath.trim().isEmpty()) {
                        System.out.println(BRIGHT_RED + "✗ Error: File path cannot be empty. Please try again." + RESET);
                        continue;
                    }

                    System.out.println(BRIGHT_PURPLE + "\n✓ Loading puzzle file..." + RESET);

                    try {
                        long startReadTime = System.currentTimeMillis();
                        initialBoard = readInputFile(filePath);
                        long endReadTime = System.currentTimeMillis();

                        System.out.println(BRIGHT_GREEN + "✓ File loaded successfully in " +
                                (endReadTime - startReadTime) + "ms!" + RESET);
                    } catch (IOException e) {
                        System.out.println(BRIGHT_RED + "✗ Error loading file: " + e.getMessage() + RESET);

                        boolean validTryAgainChoice = false;
                        while (!validTryAgainChoice) {
                            System.out.println(BRIGHT_CYAN + "Do you want to try another file? (y/n)" + RESET);
                            System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                            String tryAgain = scanner.nextLine();

                            if (tryAgain.equalsIgnoreCase("y")) {
                                validTryAgainChoice = true;
                            } else if (tryAgain.equalsIgnoreCase("n")) {
                                validTryAgainChoice = true;
                                continueProgram = false;
                            } else {
                                System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter 'y' or 'n'." + RESET);
                            }
                        }
                        continue;
                    }
                }

                System.out.println("\n" + BOLD + BRIGHT_CYAN + "⬢ Initial Board:" + RESET);
                printBoardWithFrame(initialBoard);

                boolean validAlgorithmChoice = false;
                int algorithmChoice = 0;

                while (!validAlgorithmChoice) {
                    System.out.println("\n" + BOLD + BRIGHT_CYAN + "⬢ Select Algorithm:" + RESET);
                    printAlgorithmMenu();

                    System.out.print(BRIGHT_YELLOW + "➤ Enter your choice (1-4): " + RESET);
                    String algorithmInput = scanner.nextLine();

                    try {
                        algorithmChoice = Integer.parseInt(algorithmInput);
                        if (algorithmChoice >= 1 && algorithmChoice <= 4) {
                            validAlgorithmChoice = true;
                        } else {
                            System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter a number between 1 and 4." + RESET);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter a valid number." + RESET);
                    }
                }

                int heuristicChoice = 1;
                if (algorithmChoice != 2) {
                    boolean validHeuristicChoice = false;

                    while (!validHeuristicChoice) {
                        System.out.println("\n" + BOLD + BRIGHT_CYAN + "⬢ Select Heuristic:" + RESET);
                        printHeuristicMenu();

                        System.out.print(BRIGHT_YELLOW + "➤ Enter your choice (1-2): " + RESET);
                        String heuristicInput = scanner.nextLine();

                        try {
                            heuristicChoice = Integer.parseInt(heuristicInput);
                            if (heuristicChoice >= 1 && heuristicChoice <= 2) {
                                validHeuristicChoice = true;
                            } else {
                                System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter either 1 or 2." + RESET);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter a valid number." + RESET);
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
                        System.out.println("\n" + BRIGHT_CYAN + "Enter beam width (recommended: 3-5):" + RESET);
                        System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                        String beamWidthInput = scanner.nextLine();

                        try {
                            beamWidth = Integer.parseInt(beamWidthInput);
                            if (beamWidth >= 1) {
                                validBeamWidth = true;
                            } else {
                                System.out.println(BRIGHT_RED + "✗ Beam width must be a positive integer." + RESET);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter a valid number." + RESET);
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

                System.out.println("\n" + BRIGHT_PURPLE + "⬢ Running " + algorithmName +
                        (algorithmChoice != 2 ? " with " + heuristicName + " heuristic" : "") +
                        "..." + RESET);

                printLoadingAnimation();

                long startTime = System.currentTimeMillis();
                Solution solution = solver.solve();
                long endTime = System.currentTimeMillis();
                long executionTime = endTime - startTime;

                System.out.println("\n" + BOLD + BG_BLUE + "══════════ RESULTS ══════════" + RESET);
                System.out.println(BOLD + BRIGHT_CYAN + "⬢ Algorithm: " + RESET + BRIGHT_WHITE + algorithmName + RESET);

                if (algorithmChoice != 2) {
                    System.out.println(BOLD + BRIGHT_CYAN + "⬢ Heuristic: " + RESET + BRIGHT_WHITE + heuristicName + RESET);
                }

                System.out.println(BOLD + BRIGHT_CYAN + "⬢ Execution time: " + RESET + BRIGHT_WHITE + executionTime + " ms" + RESET);
                System.out.println(BOLD + BRIGHT_CYAN + "⬢ Nodes visited: " + RESET + BRIGHT_WHITE + solver.getNodesVisited() + RESET);

                if (solution != null) {
                    int moveCount = solution.getMoves().size();
                    System.out.println(BOLD + BRIGHT_GREEN + "\n✓ SOLUTION FOUND! " + RESET +
                            BRIGHT_WHITE + "Number of moves: " + moveCount + RESET);

                    System.out.println(BOLD + BG_BLUE + "══════════ SOLUTION STEPS ══════════" + RESET);

                    solution.printStepsAnimated();

                    boolean validSaveChoice = false;
                    while (!validSaveChoice) {
                        System.out.println("\n" + BRIGHT_CYAN + "⬢ Do you want to save the solution to a file? (y/n)" + RESET);
                        System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                        String saveToFile = scanner.nextLine();

                        if (saveToFile.equalsIgnoreCase("y") || saveToFile.equalsIgnoreCase("n")) {
                            validSaveChoice = true;

                            if (saveToFile.equalsIgnoreCase("y")) {
                                boolean validOutputPath = false;
                                while (!validOutputPath) {
                                    System.out.println(BRIGHT_CYAN + "Enter output file path (.txt):" + RESET);
                                    System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                                    String outputPath = scanner.nextLine();

                                    if (outputPath.trim().isEmpty()) {
                                        System.out.println(BRIGHT_RED + "✗ Output path cannot be empty. Please try again." + RESET);
                                    } else {
                                        try {
                                            solution.saveToFile(outputPath);
                                            System.out.println(BRIGHT_GREEN + "✓ Solution saved to " + outputPath + RESET);
                                            validOutputPath = true;
                                        } catch (IOException e) {
                                            System.out.println(BRIGHT_RED + "✗ Error saving file: " + e.getMessage() +
                                                    ". Please try another path." + RESET);
                                        }
                                    }
                                }
                            }
                        } else {
                            System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter 'y' or 'n'." + RESET);
                        }
                    }
                } else {
                    System.out.println(BOLD + BRIGHT_RED + "\n✗ NO SOLUTION FOUND!" + RESET);
                    System.out.println(YELLOW + "The puzzle may be unsolvable or require more resources than available." + RESET);
                }

                boolean validContinueChoice = false;
                while (!validContinueChoice) {
                    System.out.println("\n" + BRIGHT_CYAN + "⬢ Do you want to run another test? (y/n)" + RESET);
                    System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                    String continueChoice = scanner.nextLine();

                    if (continueChoice.equalsIgnoreCase("y")) {
                        validContinueChoice = true;
                    } else if (continueChoice.equalsIgnoreCase("n")) {
                        validContinueChoice = true;
                        continueProgram = false;
                    } else {
                        System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter 'y' or 'n'." + RESET);
                    }
                }

                if (continueProgram) {
                    if (initialBoard != null) {
                        boolean validReuseChoice = false;
                        while (!validReuseChoice) {
                            System.out.println("\n" + BRIGHT_CYAN + "⬢ Do you want to reuse the same puzzle file? (y/n)" + RESET);
                            System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                            String reuseChoice = scanner.nextLine();

                            if (reuseChoice.equalsIgnoreCase("y")) {
                                validReuseChoice = true;

                                System.out.println(BRIGHT_GREEN + "✓ Reusing the same puzzle file." + RESET);

                                System.out.println("\n" + BOLD + BRIGHT_CYAN + "⬢ Initial Board:" + RESET);
                                printBoardWithFrame(initialBoard);
                            } else if (reuseChoice.equalsIgnoreCase("n")) {
                                validReuseChoice = true;
                                initialBoard = null;
                                continue;
                            } else {
                                System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter 'y' or 'n'." + RESET);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(BRIGHT_RED + "✗ Unexpected error: " + e.getMessage() + RESET);
                e.printStackTrace();

                boolean validContinueAfterErrorChoice = false;
                while (!validContinueAfterErrorChoice) {
                    System.out.println(BRIGHT_CYAN + "Do you want to continue? (y/n)" + RESET);
                    System.out.print(BRIGHT_YELLOW + "➤ " + RESET);
                    String continueAfterError = scanner.nextLine();

                    if (continueAfterError.equalsIgnoreCase("y")) {
                        validContinueAfterErrorChoice = true;
                    } else if (continueAfterError.equalsIgnoreCase("n")) {
                        validContinueAfterErrorChoice = true;
                        continueProgram = false;
                    } else {
                        System.out.println(BRIGHT_RED + "✗ Invalid input. Please enter 'y' or 'n'." + RESET);
                    }
                }
            }
        }

        System.out.println("\n" + BOLD + BG_PURPLE + "══════════ THANK YOU FOR USING RUSH HOUR SOLVER! ══════════" + RESET);
        scanner.close();
    }

    private static void printWelcomeBanner() {
        String[] banner = {
                "╔═══════════════════════════════════════════════════════════════════════════╗",
                "║                                                                           ║",
                "║  ██████╗ ██╗   ██╗███████╗██╗  ██╗    ██╗  ██╗ ██████╗ ██╗   ██╗██████╗   ║",
                "║  ██╔══██╗██║   ██║██╔════╝██║  ██║    ██║  ██║██╔═══██╗██║   ██║██╔══██╗  ║",
                "║  ██████╔╝██║   ██║███████╗███████║    ███████║██║   ██║██║   ██║██████╔╝  ║",
                "║  ██╔══██╗██║   ██║╚════██║██╔══██║    ██╔══██║██║   ██║██║   ██║██╔══██╗  ║",
                "║  ██║  ██║╚██████╔╝███████║██║  ██║    ██║  ██║╚██████╔╝╚██████╔╝██║  ██║  ║",
                "║  ╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝    ╚═╝  ╚═╝ ╚═════╝  ╚═════╝ ╚═╝  ╚═╝  ║",
                "║                                                                           ║",
                "║              ██████╗ ██╗   ██╗███████╗███████╗██╗     ███████╗            ║",
                "║              ██╔══██╗██║   ██║╚══███╔╝╚══███╔╝██║     ██╔════╝            ║",
                "║              ██████╔╝██║   ██║  ███╔╝   ███╔╝ ██║     █████╗              ║",
                "║              ██╔═══╝ ██║   ██║ ███╔╝   ███╔╝  ██║     ██╔══╝              ║",
                "║              ██║     ╚██████╔╝███████╗███████╗███████╗███████╗            ║",
                "║              ╚═╝      ╚═════╝ ╚══════╝╚══════╝╚══════╝╚══════╝            ║",
                "║                                                                           ║",
                "║                     SOLVER WITH PATHFINDING ALGORITHMS                    ║",
                "║                                                                           ║",
                "╚═══════════════════════════════════════════════════════════════════════════╝"
        };

         // Print each line with different colors for a rainbow effect
        String[] colors = {BRIGHT_RED, BRIGHT_YELLOW, BRIGHT_GREEN, BRIGHT_CYAN, BRIGHT_BLUE, BRIGHT_PURPLE};
        for (int i = 0; i < banner.length; i++) {
            System.out.println(colors[i % colors.length] + banner[i] + RESET);
        }

        System.out.println("\n" + BRIGHT_CYAN + "IF2211 Strategi Algoritma - Tugas Kecil 3" + RESET);
        System.out.println(YELLOW + "Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding" + RESET);
        System.out.println(BRIGHT_GREEN + "--------------------------------------------------------------" + RESET);
    }

    private static void printAlgorithmMenu() {
        System.out.println(BRIGHT_GREEN + "╔════╦═══════════════════════════════════════╗" + RESET);
        System.out.println(BRIGHT_GREEN + "║" + BRIGHT_YELLOW + " 1  " + BRIGHT_GREEN + "║" + BRIGHT_WHITE + " Greedy Best First Search              " + BRIGHT_GREEN + "║" + RESET);
        System.out.println(BRIGHT_GREEN + "╠════╬═══════════════════════════════════════╣" + RESET);
        System.out.println(BRIGHT_GREEN + "║" + BRIGHT_YELLOW + " 2  " + BRIGHT_GREEN + "║" + BRIGHT_WHITE + " Uniform Cost Search (UCS)             " + BRIGHT_GREEN + "║" + RESET);
        System.out.println(BRIGHT_GREEN + "╠════╬═══════════════════════════════════════╣" + RESET);
        System.out.println(BRIGHT_GREEN + "║" + BRIGHT_YELLOW + " 3  " + BRIGHT_GREEN + "║" + BRIGHT_WHITE + " A* Search                             " + BRIGHT_GREEN + "║" + RESET);
        System.out.println(BRIGHT_GREEN + "╠════╬═══════════════════════════════════════╣" + RESET);
        System.out.println(BRIGHT_GREEN + "║" + BRIGHT_YELLOW + " 4  " + BRIGHT_GREEN + "║" + BRIGHT_WHITE + " Beam Search (Bonus)                   " + BRIGHT_GREEN + "║" + RESET);
        System.out.println(BRIGHT_GREEN + "╚════╩═══════════════════════════════════════╝" + RESET);
    }

    private static void printHeuristicMenu() {
        System.out.println(BRIGHT_BLUE + "╔════╦═══════════════════════════════════════╗" + RESET);
        System.out.println(BRIGHT_BLUE + "║" + BRIGHT_YELLOW + " 1  " + BRIGHT_BLUE + "║" + BRIGHT_WHITE + " Manhattan Distance to Exit            " + BRIGHT_BLUE + "║" + RESET);
        System.out.println(BRIGHT_BLUE + "╠════╬═══════════════════════════════════════╣" + RESET);
        System.out.println(BRIGHT_BLUE + "║" + BRIGHT_YELLOW + " 2  " + BRIGHT_BLUE + "║" + BRIGHT_WHITE + " Blocking Vehicles Count               " + BRIGHT_BLUE + "║" + RESET);
        System.out.println(BRIGHT_BLUE + "╚════╩═══════════════════════════════════════╝" + RESET);
    }

    private static void printLoadingAnimation() {
        String[] frames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        try {
            for (int i = 0; i < 20; i++) {
                System.out.print("\r" + BRIGHT_CYAN + frames[i % frames.length] +
                        " Solving puzzle... " + (i * 5) + "%" + RESET);
                Thread.sleep(100);
            }
            System.out.println("\r" + BRIGHT_GREEN + "✓ Puzzle solved!                 " + RESET);
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
                System.out.println(BRIGHT_GREEN + "K " + BRIGHT_RED + "PP (Berhasil Keluar)" + RESET);
            } else {
                System.out.println(BRIGHT_GREEN + "K" + RESET);
            }
        }

        if (exitSide == 'L') {
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
                    System.out.print(BRIGHT_GREEN + "K " + BRIGHT_RED + "PP " + RESET);
                } else {
                    System.out.print(BRIGHT_GREEN + "K" + RESET);
                }
            } else {
                if (exitSide == 'L') {
                    System.out.print(" ");
                }
            }
            
            System.out.print(BRIGHT_CYAN + "║" + RESET);

            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if (cell == 'P') {
                    System.out.print(BRIGHT_RED + cell + RESET);
                } else if (cell == '.') {
                    System.out.print(BRIGHT_WHITE + "·" + RESET);
                } else if (cell == 'K') {
                    System.out.print(BRIGHT_WHITE + "·" + RESET);
                } else {
                    System.out.print(BRIGHT_BLUE + cell + RESET);
                }
            }

            if (exitSide == 'R' && i == primaryRow) {
                System.out.print(BRIGHT_CYAN + "║" + RESET + BRIGHT_GREEN + "K" + RESET);
                if (isSolved) {
                    System.out.print(" " + BRIGHT_RED + "PP (Berhasil Keluar)" + RESET);
                }
            } else {
                System.out.print(BRIGHT_CYAN + "║" + RESET);
            }
            
            if (exitSide == 'L' && i == primaryRow && isSolved) {
                System.out.print(BRIGHT_RED + "(Berhasil Keluar)" + RESET);
            }
            
            System.out.println();
        }

        if (exitSide == 'L') {
            System.out.print(" " + BRIGHT_CYAN + "╚");
        } else {
            System.out.print(BRIGHT_CYAN + "╚");
        }

        for (int j = 0; j < cols; j++) {
            System.out.print("═");
        }
        System.out.println("╝" + RESET);

        if (exitSide == 'B') {
            System.out.print(" ");
            
            for (int j = 0; j < primaryCol; j++) {
                System.out.print(" ");
            }
            
            if (isSolved) {
                System.out.print(BRIGHT_GREEN + "K " + BRIGHT_RED + "PP (Berhasil Keluar)" + RESET);
            } else {
                System.out.print(BRIGHT_GREEN + "K" + RESET);
            }
            System.out.println();
        }

        System.out.println(BRIGHT_RED + "■ " + RESET + "Primary Vehicle (P)  " +
                BRIGHT_GREEN + "■ " + RESET + "Exit (K)  " +
                BRIGHT_BLUE + "■ " + RESET + "Other Vehicles");
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
                if (currentLine == null) {
                    throw new IOException("Invalid board dimensions");
                }

                String trimmed = currentLine.trim();
                boolean leftExit = trimmed.startsWith("K");
                boolean rightExit = trimmed.endsWith("K") && trimmed.length() > 1;

                int nonSpaceCount = 0;
                for (char c : currentLine.toCharArray()) {
                    if (c != ' ') nonSpaceCount++;
                }
                if (leftExit) nonSpaceCount--;
                if (rightExit) nonSpaceCount--;

                if (nonSpaceCount < cols) {
                    throw new IOException("Invalid board dimensions");
                }
                if (nonSpaceCount > cols) {
                    throw new IOException("Invalid board dimensions");
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
            if (bottomLine != null && bottomLine.trim().length() > 0) {
                if (bottomLine.trim().indexOf('K') >= 0 && bottomLine.trim().length() <= cols) {
                    originalLines.add(bottomLine);
                    exitSide = 'B';
                    externalKCol = bottomLine.indexOf('K');
                    String afterBottomK = reader.readLine();
                    while (afterBottomK != null && afterBottomK.trim().isEmpty()) {
                        afterBottomK = reader.readLine();
                    }
                    if (afterBottomK != null) {
                        throw new IOException("Invalid board dimensions");
                    }
                } else {
                    throw new IOException("Invalid board dimensions");
                }
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
                throw new IOException("No exit (K) found at any edge");
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
                System.out.println(YELLOW + "Warning: Number of non-primary vehicles in file (" +
                        numNonPrimaryVehicles + ") doesn't match actual count (" +
                        actualVehicleCount + ")" + RESET);
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