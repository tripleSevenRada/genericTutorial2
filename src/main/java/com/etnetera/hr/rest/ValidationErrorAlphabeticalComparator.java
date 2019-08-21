package com.etnetera.hr.rest;

import java.util.Comparator;

public class ValidationErrorAlphabeticalComparator
        implements Comparator <ValidationError> {

    @Override
    public int compare(ValidationError veLeft, ValidationError veRight) {
        String left = (isAllNonNull(veLeft)) ?
                veLeft.getField().trim() + veLeft.getMessage().trim(): "";
        String right = (isAllNonNull((veRight))) ?
                veRight.getField().trim() + veRight.getMessage().trim(): "";
        return (left.compareTo(right));
    }

    private boolean isAllNonNull(ValidationError ve){
        return ve != null && ve.getMessage() !=null && ve.getField() != null;
    }
}
