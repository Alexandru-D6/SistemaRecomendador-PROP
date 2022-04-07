package Exceptions;

// Used in the ImageFinder in order to signal that the code should try again with a different key 
public class TryAgainException extends Exception {
    public TryAgainException() {}
}
