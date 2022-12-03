package com.cookos.net;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerMessage implements Serializable {
    AnswerType answerType;
    String message;
}
