package Exceptions;

public class ObjectDoesNotExistException extends DBException {
    public ObjectDoesNotExistException(Class<? extends Object> accessedClass) {
        super(accessedClass);
    }
}
