package de.qaware.alexa.warehouse.business;

/**
 * A warehouse.
 */
public interface WarehouseService {
    /**
     * Returns the amount of the given ware in the warehouse.
     *
     * @param ware Ware.
     * @return Amount.
     * @throws UnknownWareException If the ware is not known.
     */
    int getAmount(String ware) throws UnknownWareException;

    /**
     * Orders a ware.
     *
     * @param ware Ware.
     * @throws UnknownWareException If the ware is not known.
     */
    void orderWare(String ware) throws UnknownWareException;

    /**
     * Locates a ware.
     *
     * @param ware Ware.
     * @return Ware location.
     * @throws UnknownWareException If the ware is not known.
     */
    Location locateWare(String ware) throws UnknownWareException;
}
