package com.cookos.util;

import java.util.List;

import com.cookos.model.Identifiable;
import com.cookos.model.Subject;
import com.cookos.model.SubjectForSpeciality;

public class CastHelpers {
    
    public static <T> List<Identifiable> toIdentifiables(List<T> objects) {
        return objects.stream().map(Identifiable.class::cast).toList();
    }

    public static List<SubjectForSpeciality> toSubjectForSpeciality(List<Subject> subjects) {
        return subjects.stream().map(s -> new SubjectForSpeciality(s)).toList();
    }
}
