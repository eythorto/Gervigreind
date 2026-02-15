public class State {
    public enum Square {
        EMPTY,
        BLOCKED,
        BLACK,
        WHITE
    }
    public Square[][] board;
    public String role;
    public State(Square[][] board, String role) {
        this.board = board;
        this.role = role;
    }
}
