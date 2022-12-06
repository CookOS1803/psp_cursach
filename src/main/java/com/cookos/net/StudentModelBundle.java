package com.cookos.net;

import java.io.Serializable;
import java.util.List;

import com.cookos.model.*;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentModelBundle implements Serializable {
    private Student student;
    private User user;
    private List<Performance> performance;
}
