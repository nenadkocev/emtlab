package emt.fcse.laboratorisa.validators;

import emt.fcse.laboratorisa.Model.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserDto> {
   public void initialize(PasswordMatches constraint) {
   }

   public boolean isValid(UserDto obj, ConstraintValidatorContext context) {
      return obj.getPassword().equals(obj.getMatchingPassword());
   }
}
