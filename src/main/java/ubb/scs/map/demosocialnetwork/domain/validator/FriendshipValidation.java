package ubb.scs.map.demosocialnetwork.domain.validator;

import ubb.scs.map.demosocialnetwork.domain.Friendship;

public class FriendshipValidation implements Validator <Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getIdUser1().equals(entity.getIdUser2())){
            throw new ValidationException("You are already a friends!");
        }
        if(entity.getIdUser1() <= 0){
            throw new ValidationException("Your first user does not exist!");
        }
        if(entity.getIdUser2() <= 0){
            throw new ValidationException("Your second user does not exist!");
        }
        if(!entity.getStatus().equals("PENDING")  && !entity.getStatus().equals("ACCEPTED") && !entity.getStatus().equals("DECLINED")){
            throw new ValidationException("Your friendship status is not accepted!");
        }
    }
}
