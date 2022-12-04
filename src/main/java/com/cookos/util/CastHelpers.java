package com.cookos.util;

import java.util.List;

import com.cookos.model.Identifiable;

public class CastHelpers {
    
    public static <T> List<Identifiable> toIdentifiables(List<T> objects) {
        return objects.stream().map(Identifiable.class::cast).toList();
    }
}
