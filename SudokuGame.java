import java.awt.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.Border;

public class SudokuGame extends JFrame {
    private final JTextField[][] cells = new JTextField[9][9];
    private final int[][] solution;

    public SudokuGame(int[][] puzzle, int[][] solution) {
        this.solution = solution;

        setTitle("Sudoku Game");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 9, 1, 1)); // spacing between cells
        getContentPane().setBackground(Color.BLACK); // grid line color

        Font font = new Font("SansSerif", Font.BOLD, 20);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                JTextField tf = new JTextField();
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setFont(font);
                tf.setBorder(createCellBorder(row, col));
                tf.setBackground(Color.WHITE);

                if (puzzle[row][col] != 0) {
                    tf.setText(String.valueOf(puzzle[row][col]));
                    tf.setEditable(false);
                    tf.setBackground(new Color(220, 220, 220)); // light gray
                }

                cells[row][col] = tf;
                add(tf);
            }
        }

        JMenuBar menuBar = new JMenuBar();
        JButton checkButton = new JButton("Check");
        checkButton.addActionListener(e -> checkSolution());
        menuBar.add(checkButton);
        setJMenuBar(menuBar);
    }

    private Border createCellBorder(int row, int col) {
        int top = (row % 3 == 0) ? 3 : 1;
        int left = (col % 3 == 0) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int right = (col == 8) ? 3 : 1;
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
    }

    private void checkSolution() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = cells[row][col].getText();
                if (text.isEmpty() || !text.matches("[1-9]")) {
                    JOptionPane.showMessageDialog(this, "Invalid input at [" + (row + 1) + "," + (col + 1) + "]");
                    return;
                }

                int val = Integer.parseInt(text);
                if (val != solution[row][col]) {
                    JOptionPane.showMessageDialog(this, "Incorrect solution.");
                    return;
                }
            }
        }

        JOptionPane.showMessageDialog(this, "🎉 Congratulations! You solved it!");
    }

    
    public static void main(String[] args) {
        int[][] puzzle = generateBoard(40); // 40 blank cells = medium difficulty
        int[][] solution = copyBoard(puzzle);
        solve(solution);

        SwingUtilities.invokeLater(() -> {
            SudokuGame game = new SudokuGame(puzzle, solution);
            game.setVisible(true);
        });
    }


    private static int[][] board = new int[9][9];
    private static final Random random = new Random();

    public static int[][] generateBoard(int emptyCells) {
        board = new int[9][9];
        fillDiagonal();
        solve(board);
        removeDigits(emptyCells);
        return copyBoard(board);
    }

    private static void fillDiagonal() {
        for (int i = 0; i < 9; i += 3) {
            fillBox(i, i);
        }
    }

    private static void fillBox(int row, int col) {
        int num;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                do {
                    num = random.nextInt(9) + 1;
                } while (!isUnusedInBox(row, col, num));
                board[row + i][col + j] = num;
            }
        }
    }

    private static boolean isUnusedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[rowStart + i][colStart + j] == num)
                    return false;
        return true;
    }

    private static void removeDigits(int count) {
        while (count > 0) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);
            if (board[row][col] != 0) {
                board[row][col] = 0;
                count--;
            }
        }
    }

    private static boolean isSafe(int[][] board, int row, int col, int num) {
        for (int d = 0; d < 9; d++) {
            if (board[row][d] == num || board[d][col] == num ||
                board[row - row % 3 + d / 3][col - col % 3 + d % 3] == num)
                return false;
        }
        return true;
    }

    public static boolean solve(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (solve(board))
                                return true;
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static int[][] copyBoard(int[][] src) {
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            System.arraycopy(src[i], 0, copy[i], 0, 9);
        }
        return copy;
    }
}
