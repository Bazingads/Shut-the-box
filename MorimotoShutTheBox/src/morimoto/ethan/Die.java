package morimoto.ethan;

import java.util.Random;

/**
 * Purpose: Class simulating a 6 sided dice
 * @author Ethan Morimoto
 * @date January 8, 2025
 */
public class Die {
    private int value;    
    private int numSides; 

    /**
     * Constructor without a specified number of sides.
     */
    public Die() {
        numSides = 6;
        value = roll();
    }

    /**
     * Constructor with a specified number of sides.
     * @param n The number of sides for the die
     */
    public Die(int n) {
        numSides = n;
        value = roll();
    }

    /**
     * Rolls the die and returns a value from 1-6.
     * @return value The value rolled
     */
    public int roll() {
        Random rand = new Random();
        value = rand.nextInt(1, numSides + 1);
        return value;
    }

    /**
     * Sets the number of sides on the die.
     * @param n The new number of sides for the die
     */
    public void setSides(int n) {
        numSides = n;
    }
}
