package org.example.server.exception;

public class OutOfStockException extends StoreException{
    public OutOfStockException(String message) {
        super(message);
    }
}
