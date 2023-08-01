package com.ciandt.flow.core.parser;

import com.ciandt.flow.settings.OpenAISettingsState;
import com.ciandt.flow.util.HtmlUtil;
import com.google.api.client.json.Json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import com.ciandt.flow.core.ConversationManager;
import com.ciandt.flow.ui.MessageComponent;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

/**
 * @author Wuzi
 */
public class OfficialParser {

    public static ParseResult parserBeesAI(String response) {
        ParseResult parseResult = new ParseResult();
        parseResult.source = response;
        parseResult.html = HtmlUtil.md2html(response);
        return parseResult;
    }

    public static ParseResult parseFlowResponse(String response) {
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        String content = object.get("content").toString();

        ParseResult parseResult = new ParseResult();
        parseResult.source = content;
        parseResult.html = HtmlUtil.md2html(content);
        return parseResult;
    }

    public static ParseResult parseGPT35Turbo(String response) {
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        JsonArray choices = object.get("choices").getAsJsonArray();
        StringBuilder result = new StringBuilder();
        for (JsonElement element : choices) {
            JsonObject messages = element.getAsJsonObject().get("message").getAsJsonObject();
            String content = messages.get("content").getAsString();
            result.append(content);
        }

        OpenAISettingsState state = OpenAISettingsState.getInstance();
        StringBuilder usageResult = new StringBuilder(result);
        if (state.enableTokenConsumption) {
            JsonObject usage = object.get("usage").getAsJsonObject();
            usageResult.append("<br /><br />");
            usageResult.append("*");
            usageResult.
                    append("Prompt tokens: ").append("<b>").append(usage.get("prompt_tokens").getAsInt()).append("</b>").append(", ").
                    append("Completion tokens: ").append("<b>").append(usage.get("completion_tokens").getAsInt()).append("</b>").append(", ").
                    append("Total tokens: ").append("<b>").append(usage.get("total_tokens").getAsInt()).append("</b>");
            usageResult.append("*");
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = result.toString();
        parseResult.html = HtmlUtil.md2html(usageResult.toString());
        return parseResult;
    }

    public static ParseResult parseGPT35TurboWithStream(MessageComponent component, String response) {
        JsonObject object = JsonParser.parseString(response).getAsJsonObject();
        JsonArray choices = object.get("choices").getAsJsonArray();
        StringBuilder result = new StringBuilder();
        for (JsonElement element : choices) {
            JsonObject messages = element.getAsJsonObject().get("delta").getAsJsonObject();
            if (!messages.keySet().contains("content")) {
                continue;
            }
            String content = messages.get("content").getAsString();
            result.append(content);
            component.getAnswers().add(result.toString());
        }
        ParseResult parseResult = new ParseResult();
        parseResult.source = component.prevAnswers();
        parseResult.html = HtmlUtil.md2html(component.prevAnswers());
        return parseResult;
    }

    public static class ParseResult {
        private String source;
        private String html;

        public String getSource() {
            return source;
        }

        public String getHtml() {
            return html;
        }
    }

}
