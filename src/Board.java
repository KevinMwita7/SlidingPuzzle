import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdRandom;

public class Board {
    private int[][] board;
    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException();
        int n = tiles.length;
        board = new int[n][n];
        for (int i = 0; i < n; ++ i) {
            for (int j = 0; j < n; ++j) {
                board[i][j] = tiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEWLINE = System.lineSeparator();
        int n = dimension();
        sb.append(n);
        for (int i = 0; i < n; ++ i) {
            sb.append(NEWLINE);
            for (int j = 0; j < n; ++j) {
                sb.append(board[i][j]).append(" ");
            }
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return board.length;
    }

    // number of tiles out of place
    public int hamming() {
        int n = dimension();
        int count = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (board[i][j] > 0 && cell(i, j) != board[i][j]) {
                    ++count;
                }
            }
        }
        return count;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int n = dimension();
        int sum = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (board[i][j] > 0 && cell(i, j) != board[i][j]) {
                    int[] coords = coordinates(board[i][j]);
                    sum += Math.abs(coords[0] - i) + Math.abs(coords[1] - j);
                }
            }
        }
        return sum;
    }

    // Given the row and column, return the cell's value
    // e.g. 0,0 is cell 1 and 2,1 is cell 8
    private int cell(int row, int col) {
        int n = dimension();
        if (row == n - 1 && col == n - 1) return 0;
        return row * n + col + 1;
    }

    // Given cell value, get its row and col
    private int[] coordinates(int cell) {
        // Deduct 1 since we are using 1 based indices
        // instead of 0 based
        --cell;
        int row = cell / dimension();
        int col = cell - (row * dimension());
        return new int[]{row, col};
    }

    // does this board equal y?
    public boolean equals(Object y) {
        // If the object is compared with itself then return true
        if (y == this) {
            return true;
        }

        // Check if o is an instance of Board or not
        if (!(y instanceof Board)) {
            return false;
        }

        // typecast y to Board so that we can compare data members
        Board b = (Board) y;

        if (b.dimension() != this.dimension()) {
            return false;
        }

        return b.toString().equals(this.toString());
    }

    // is this board the goal board?
    public boolean isGoal() {
        int n = dimension();
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                // Compare expected value with current value
                // Return false if the two don't match.
                if (cell(i, j) != board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int[] emptyCell = new int[2];
        int n = dimension();
        int[][] copy = new int[n][n];
        Queue<Board> q = new Queue<>();

        // Create a copy from which we'll construct the neighbours
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                copy[i][j] = board[i][j];
                // Note where the empty cell is
                if (board[i][j] == 0) {
                    emptyCell[0] = i;
                    emptyCell[1] = j;
                }
            }
        }

        int[][] emptyCellNeighbours = getNeighbours(emptyCell[0], emptyCell[1]);
        for (int k = 0; k < emptyCellNeighbours.length; ++k) {
            if (emptyCellNeighbours[k].length > 0) {
                // Swap the empty cell with neighbouring cell
                exchange(copy, emptyCell[0], emptyCell[1], emptyCellNeighbours[k][0], emptyCellNeighbours[k][1]);
                Board neighbour = new Board(copy);
                q.enqueue(neighbour);
                // Swap back the empty cell with neighbouring cell to revert board to original state
                exchange(copy, emptyCell[0], emptyCell[1], emptyCellNeighbours[k][0], emptyCellNeighbours[k][1]);
            }
        }

        return q;
    }

    private int[][] getNeighbours(int row, int col) {
        int[][] neighbours = {new int[]{}, new int[]{}, new int[]{}, new int[]{}};
        int n = dimension();
        int left = col - 1;
        int right = col + 1;
        int top = row - 1;
        int btm = row + 1;

        if (left >= 0) {
            neighbours[0] = new int[]{row, left};
        }
        if (right < n) {
            neighbours[1] = new int[]{row, right};
        }
        if (top >= 0) {
            neighbours[2] = new int[]{top, col};
        }
        if (btm < n) {
            neighbours[3] = new int[]{btm, col};
        }

        return neighbours;
    }

    private void exchange(int[][] b, int i, int j, int k, int l) {
        int temp = b[i][j];
        b[i][j] = b[k][l];
        b[k][l] = temp;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int n = board.length;
        int sz = n * n;
        int[][] copy = new int[n][n];

        // Create a copy from which we'll construct the neighbours
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                copy[i][j] = board[i][j];
            }
        }

        int[] cellA;
        int[] cellB;

        // Ensure that we are not swapping empty cell
        // Also we cannot choose cells at random as
        // this might fail to produce a twin in some
        // cases especially in 2x2 boards
        if (copy[0][0] == 0 || copy[0][1] == 0) {
            cellA = coordinates(3);
            cellB = coordinates(4);
        } else {
            cellA = coordinates(1);
            cellB = coordinates(2);
        }

        // Swap the pair of cells
        exchange(copy, cellA[0], cellA[1], cellB[0], cellB[1]);
        return new Board(copy);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        /*int n = Integer.parseInt(args[0]);
        int[][] board = new int[n][n];
        board[0][0] = 1;
        board[0][1] = 0;
        board[0][2] = 3;
        board[1][0] = 4;
        board[1][1] = 2;
        board[1][2] = 5;
        board[2][0] = 7;
        board[2][1] = 8;
        board[2][2] = 6;

        Board b = new Board(board);

        System.out.println(b);
        System.out.println("---------------");
        System.out.println(b.twin());*/
        int n = 2;
        int[][] board = new int[n][n];

        board[0][0] = 2;
        board[0][1] = 1;
        board[1][0] = 3;
        board[1][1] = 0;

        Board b = new Board(board);
        System.out.println(b);
        System.out.println("-----------------");
        System.out.println(b.twin());
    }

}