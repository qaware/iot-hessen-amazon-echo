package de.qaware.alexa.warehouse.business;

import org.springframework.stereotype.Service;

/**
 * Dummy warehouse implementation with hardcoded values.
 */
@Service
public class DummyWarehouseService implements WarehouseService {
    @Override
    public int getAmount(String ware) throws UnknownWareException {
        switch (ware) {
            case "schrauben":
                return 14;
            case "winkel":
                return 5;
            default:
                throw new UnknownWareException();
        }
    }

    @Override
    public void orderWare(String ware) throws UnknownWareException {
        switch (ware) {
            case "schrauben":
            case "winkel":
                break;
            default:
                throw new UnknownWareException();
        }
    }

    @Override
    public Location locateWare(String ware) throws UnknownWareException {
        switch (ware) {
            case "schrauben":
                return new Location(8, "C", 819);
            case "winkel":
                return new Location(5, "F", 203);
            default:
                throw new UnknownWareException();
        }
    }
}
