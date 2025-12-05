package com.aidemoproject.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class AgentResponse {

	private final int statusCode;
    private final String body;
    private final List<String> conversationLog;
    private final String sessionId;
    private final String userMessage;
    private final String journeyType;

}
