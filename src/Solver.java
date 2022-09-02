import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class Solver {
    private int moves = -1;
    private SearchNode solvedNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        Board twin = initial.twin(); // Helps in detecting unsolvable boards

        // Min PQ for our main board
        MinPQ<SearchNode> pq = new MinPQ<>(new SearchNodeComparator());
        // Min PQ for twin board
        MinPQ<SearchNode> twinPq = new MinPQ<>(new SearchNodeComparator());

        // Insert initial search nodes into respective queue
        pq.insert(new SearchNode(initial, 0, null));
        twinPq.insert(new SearchNode(twin, 0, null));

        while (true) {
            // Remove board with minimum priority and insert its neighbours
            SearchNode node = pq.delMin();
            SearchNode twinNode = twinPq.delMin();

            getMoves(pq, node);
            getMoves(twinPq, twinNode);

            if (node.board.isGoal()) {
                solvedNode = node;
                moves = node.moves;
                break;
            }
            // Is an unsolvable board
            if (twinNode.board.isGoal()) {
                moves = -1;
                break;
            }
        }
    }

    private static class SearchNodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode o1, SearchNode o2) {
            int priorityA = o1.manhattan + o1.moves;
            int priorityB = o2.manhattan + o2.moves;

            if (priorityA == priorityB) return 0;

            return priorityA - priorityB;
        }
    }
    // A search node of the game consists of a board,
    // the number of moves made to reach the board,
    // and the previous search node
    private class SearchNode {
        public final Board board;
        public final int moves;
        public final int manhattan;
        private final SearchNode previousNode;

        SearchNode(Board board, int moves, SearchNode previousNode) {
            this.board = board;
            this.moves = moves;
            this.previousNode = previousNode;
            this.manhattan = board.manhattan();
        }
    }

    // Check all possible moves that can be made in a node
    private void getMoves(MinPQ<SearchNode> pq, SearchNode node) {
        for (Board neighbour: node.board.neighbors()) {
            if (node.previousNode == null) {
                pq.insert(new SearchNode(neighbour, node.moves + 1, node));
            } else if (!neighbour.equals(node.previousNode.board)) {
                // Optimization to reduce unnecessary exploration of useless search nodes.
                // When considering the neighbors of a search node, don’t enqueue a neighbor
                // if its board is the same as the board of the previous search node in the game tree.
                pq.insert(new SearchNode(neighbour, node.moves + 1, node));
            }
        }
    }


    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return moves > -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (moves == -1) return null;
        Stack<Board> stack = new Stack<>();
        SearchNode current = solvedNode;

        // Start from search node and move backwards
        // adding the boards that led to the solution
        while (current != null) {
            stack.push(current.board);
            current = current.previousNode;
        }
        return stack;
    }

    // test client (see below)
    public static void main(String[] args) {
        /*int n = Integer.parseInt(args[0]);
        int[][] board = new int[n][n];
        board[0][0] = 0;
        board[0][1] = 1;
        board[0][2] = 3;
        board[1][0] = 4;
        board[1][1] = 2;
        board[1][2] = 5;
        board[2][0] = 7;
        board[2][1] = 8;
        board[2][2] = 6;*/

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver sol = new Solver(initial);

        // print solution to standard output
        if (!sol.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + sol.moves());
            for (Board b : sol.solution()) {
                System.out.println(b);
            }
        }
    }
}
