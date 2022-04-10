package lu.uni.student.shoppinglist;

public class ExtrasNotFoundException extends RuntimeException {
    public ExtrasNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
