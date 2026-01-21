import java.util.Objects;

class State {
    // TODO: add other attributes that store necessary information about a state of the environment
    // Only information that can change over time should be kept here.
    public final boolean turnedOn;

    public State(boolean turnedOn) {
        // TODO: add other attributes that store necessary information about a state of the environment
        this.turnedOn = turnedOn;
    }

    @Override
    public String toString() {
        // TODO: modify as needed
        return String.format("State(%s)", turnedOn);
    }

    @Override
    public int hashCode() {
        // TODO: modify as needed
        return Objects.hash(turnedOn);
    }

    @Override
    public boolean equals(Object o) {
        // TODO: modify as needed
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return turnedOn == state.turnedOn;
    }
}
