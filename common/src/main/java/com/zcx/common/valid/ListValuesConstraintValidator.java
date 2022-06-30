package com.zcx.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValuesConstraintValidator implements ConstraintValidator<ListValues,Integer> {

    private Set<Integer>set=new HashSet<>();

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        if (set.contains(value))
        {
            return true;
        }
        return false;
    }

    @Override
    public void initialize(ListValues constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        for (int value : values) {
            set.add(value);
        }

        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}




