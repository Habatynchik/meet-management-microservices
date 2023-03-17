package ua.habatynchik.gatewayservice;

public class HeaderParseException extends  Exception{
    public HeaderParseException() {
    }

    public HeaderParseException(String message) {
        super(message);
    }
}
