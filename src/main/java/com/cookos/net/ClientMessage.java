package com.cookos.net;

import java.io.Serializable;

import com.cookos.model.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientMessage implements Serializable {
    private OperationType operationType;
    private Model value;
}
