package ubb.scs.map.demosocialnetwork.domain.validator;

public interface Validator<T>{
    void validate(T entity) throws ValidationException;
}
