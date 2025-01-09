package morimoto.ethan;

/**
 * Purpose: Class of a tile in shut the box
 * @author Ethan Morimoto
 * @date January 8, 2025
 */
public class Tile {
	private int value;
	private boolean isUp;
	
	/**
     * Constructor to initialize the tile with a specific value.
     * @param n The value of the tile
     */
    public Tile(int n) {
        value = n;
        isUp = true;
    }

    /**
     * gets the value of the tile.
     * @return value The value of the tile
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks whether the tile is currently up.
     * @return isUp Returns true or false
     */
    public boolean isUp() {
        return isUp;
    }

    /**
     * Sets the tile down
     */
    public void putDown() {
        isUp = false;
    }

    /**
     * Returns a string of the tile, showing its value and state.
     * @return response A string representing the tile's value and state
     */
    @Override
    public String toString() {
        String response;
        if (isUp) {
            response = value + "(U) ";
        } else {
            response = value + "(D) ";
        }
        return response;
    }
}
