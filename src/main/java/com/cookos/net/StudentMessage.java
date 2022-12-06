package com.cookos.net;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentMessage implements Serializable {
    private StudentOperationType operationType;
    private Object value;
}
