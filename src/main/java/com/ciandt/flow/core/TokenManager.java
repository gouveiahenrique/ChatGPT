package com.ciandt.flow.core;

import com.ciandt.flow.settings.OpenAISettingsState;
import com.intellij.openapi.application.ApplicationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Wuzi
 */
public class TokenManager {
    private final Map<String,String> headers = new HashMap<>();
    private OpenAISettingsState settings = OpenAISettingsState.getInstance();
    public static TokenManager getInstance() {
        return ApplicationManager.getApplication().getService(TokenManager.class);
    }

    public Map<String, String> getFlowHeaders() {
        headers.put("Authorization", "Bearer " + settings.apiKey);
        headers.put("Content-Type","application/json; charset=utf-8");
        headers.put("Host", "flow.ciandt.com");
        return headers;
    }
}
