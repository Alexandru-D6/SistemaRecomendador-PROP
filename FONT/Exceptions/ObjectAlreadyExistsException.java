package Exceptions;

public class ObjectAlreadyExistsException extends DBException {
    public ObjectAlreadyExistsException(Class<? extends Object> accessedClass) {
        super(accessedClass);
    }
}
