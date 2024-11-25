package ubb.scs.map.demosocialnetwork.domain.validator;

import ubb.scs.map.demosocialnetwork.domain.User;

public class UserValidation implements Validator <User>{

    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getFirstName().isEmpty() || entity.getLastName().isEmpty()){
            throw new ValidationException("First name and last name cannot be empty!");
        }
        if(entity.getEmail().isEmpty()){
            throw new ValidationException("Email cannot be empty!");
        }
        if(entity.getUsername().isEmpty()){
            throw new ValidationException("Username cannot be empty!");
        }
        if(entity.getPassword().isEmpty()){
            throw new ValidationException("Password cannot be empty!");
        }
        if(entity.getPassword().length() < 6){
            throw new ValidationException("Password must be at least 6 characters!");
        }
        if(!entity.getEmail().contains("@")){
            throw new ValidationException("Email should contain @!");
        }
    }
}
