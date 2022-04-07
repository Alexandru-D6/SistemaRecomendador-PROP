package Exceptions;

public abstract class DBException extends Exception {
    Class<? extends Object> accessedClass;
    
    public DBException(Class<? extends Object> accessedClass) {
        this.accessedClass = accessedClass;
    }
    
    // Returns the class that we were trying to access when the exception was thrown
    public Class<? extends Object> getErrorClass() {
        return accessedClass;
    }
}
