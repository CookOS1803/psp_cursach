package com.cookos.model;

public enum EducationForm {
    Free,
    Paid;

    public String toRussianString() {
        return switch (this) {
            case Free -> "Бесплатная";
            case Paid -> "Платная";
        };
    }
}
