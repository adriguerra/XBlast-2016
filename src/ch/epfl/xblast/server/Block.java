package ch.epfl.xblast.server;

import java.util.NoSuchElementException;

/**
 * A block on the board
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public enum Block {
    FREE, INDESTRUCTIBLE_WALL, DESTRUCTIBLE_WALL, CRUMBLING_WALL, BONUS_BOMB(
            Bonus.INC_BOMB), BONUS_RANGE(Bonus.INC_RANGE), BONUS_SPEED(Bonus.INC_SPEED);

    private Bonus maybeAssociatedBonus;

    /**
     * Stores the argument in the maybeAssociatedBonus field
     * 
     * @param maybeAssociatedBonus
     */

    private Block(Bonus maybeAssociatedBonus) {
        this.maybeAssociatedBonus = maybeAssociatedBonus;
    }

    /**
     * Stores null in the maybeAssociatedBonus field
     */

    private Block() {
        this.maybeAssociatedBonus = null;
    }

    /**
     * Determines if a block is a free block
     * 
     * @return true if the block is free
     */
    public boolean isFree() {
        return this.equals(FREE);
    }

    /**
     * Determines if the block can host a player i.e. if it is free or a bonus
     * 
     * @return true if the block can host a player
     */

    public boolean canHostPlayer() {
        return (this.isFree() || this.isBonus());
    }

    /**
     * Determines if the block creates a shadow on the board
     * 
     * @return true if the block creates a shadow on the board
     */
    public boolean castsShadow() {
        return (this.equals(INDESTRUCTIBLE_WALL) || this.equals(DESTRUCTIBLE_WALL)
                || this.equals(CRUMBLING_WALL));
    }

    /**
     * Returns true if and only if the block represents a bonus
     * 
     * @return true if and only if the block represents a bonus
     */
    public boolean isBonus() {
        return (maybeAssociatedBonus != null);
    }

    /**
     * Returns the bonus associated to that block or throws
     * NoSuchElementException if there is none
     * 
     * @return the bonus associated to that block
     * @throws if
     *             there is no associated bonus to the block
     */
    public Bonus associatedBonus() {
        if (!isBonus()) {
            throw new NoSuchElementException();
        }
        return maybeAssociatedBonus;
    }

}
