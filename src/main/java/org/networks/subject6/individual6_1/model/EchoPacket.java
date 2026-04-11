package org.networks.subject6.individual6_1.model;

import java.io.Serializable;

public record EchoPacket(String requestMessage, String responseMessage, long durations) implements Serializable {
}
