package com.ciandt.flow.core.builder;

import com.ciandt.flow.settings.OpenAISettingsState;
import com.google.gson.JsonObject;
import com.ciandt.flow.ui.MessageGroupComponent;

/**
 * @author Wuzi
 */
public class OfficialBuilder {
    public static JsonObject buildFlowMessage(String text, MessageGroupComponent component) {
        JsonObject result = new JsonObject();
        OpenAISettingsState settingsState = OpenAISettingsState.getInstance();
        JsonObject model = new JsonObject();

        result.add("model",model);
        result.addProperty("temperature", settingsState.temperature);

        component.getMessages().add(userMessage(text));
        result.add("messages",component.getMessages());

        return result;
    }

    private static JsonObject message(String role, String text) {
        JsonObject message = new JsonObject();
        message.addProperty("role",role);
        message.addProperty("content",text);
        return message;
    }

    public static JsonObject userMessage(String text) {
        return message("user",text);
    }

    public static JsonObject systemMessage(String text) {
        return message("system",text);
    }

    public static JsonObject assistantMessage(String text) {
        return message("assistant",text);
    }
}
