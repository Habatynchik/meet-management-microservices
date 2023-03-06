package ua.habatynchik.authenticationservice.dto;

public class RegExp {
    private RegExp() {}

    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).+$";
    public static final String EMAIL = "^[A-Za-z0-9._]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

}
