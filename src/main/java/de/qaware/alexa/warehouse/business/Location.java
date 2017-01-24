package de.qaware.alexa.warehouse.business;

/**
 * Ware location.
 */
public class Location {
    private final int regal;
    private final String row;
    private final int shelf;

    /**
     * Constructor.
     *
     * @param regal Regal number.
     * @param row   Row.
     * @param shelf Shelf number.
     */
    public Location(int regal, String row, int shelf) {
        this.regal = regal;
        this.row = row;
        this.shelf = shelf;
    }

    public int getRegal() {
        return regal;
    }

    public String getRow() {
        return row;
    }

    public int getShelf() {
        return shelf;
    }
}
