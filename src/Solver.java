public abstract class Solver {
    protected Board initialBoard;
    protected int heuristicChoice;
    protected int nodesVisited;

    public Solver(Board initialBoard, int heuristicChoice) {
        this.initialBoard = initialBoard;
        this.heuristicChoice = heuristicChoice;
        this.nodesVisited = 0;
    }

    public abstract Solution solve();

    public abstract String getName();

    public int getNodesVisited() {
        return nodesVisited;
    }
}