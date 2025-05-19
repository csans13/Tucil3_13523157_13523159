import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class BeamSearch extends Solver {
    private int beamWidth;

    public BeamSearch(Board initialBoard, int heuristicChoice, int beamWidth) {
        super(initialBoard, heuristicChoice);
        this.beamWidth = beamWidth;
    }

    @Override
    public Solution solve() {
        // Menyimpan papan yang sudah dikunjungi agar tidak duplicate
        Set<Board> closedSet = new HashSet<>();

        // Pencarian dari level pertama 
        List<Node> currentLevel = new ArrayList<>();
        currentLevel.add(new Node(initialBoard, heuristicChoice));

        while (!currentLevel.isEmpty()) {
            PriorityQueue<Node> nextLevel = new PriorityQueue<>(Comparator.comparingInt(Node::getHeuristic));

            // Iterasi seluruh node di level ini
            for (Node current : currentLevel) {
                nodesVisited++;

                // Jika sudah mencapai solusi, kembalikan path-nya
                if (current.getBoard().isSolved()) {
                    return new Solution(current.getPath());
                }

                closedSet.add(current.getBoard());

                // Hasilkan semua possible moves
                List<int[]> legalMoves = current.getBoard().getLegalMoves();
                for (int[] move : legalMoves) {
                    int vehicleIdx = move[0];
                    int moveAmount = move[1];

                    Board newBoard = current.getBoard().applyMove(vehicleIdx, moveAmount);

                    // Jika papan baru sudah pernah dikunjungi, jangan diproses lagi
                    if (closedSet.contains(newBoard)) continue;
                    
                    Node successor = new Node(current, newBoard, vehicleIdx, moveAmount, heuristicChoice);
                    nextLevel.add(successor);
                }
            }

            // Seleksi node terbaik untuk level berikutnya
            currentLevel = new ArrayList<>();
            for (int i = 0; i < beamWidth && !nextLevel.isEmpty(); i++) {
                currentLevel.add(nextLevel.poll());
            }
        }

        // Jika tidak ditemukan solusi, kembalikan null
        return null;
    }

    @Override
    public String getName() {
        return "Beam Search (width=" + beamWidth + ")";
    }
}