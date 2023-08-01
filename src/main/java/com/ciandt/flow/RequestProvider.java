package com.ciandt.flow;

import com.ciandt.flow.core.TokenManager;
import com.ciandt.flow.core.builder.OfficialBuilder;
import com.ciandt.flow.ui.MainPanel;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Wuzi
 */
public class RequestProvider {
    public static final String FLOW_COMPLETIONS_API = "https://citflowdevapim.azure-api.net/api/v1/chat/completions";
    private Project myProject;
    private String url;
    private JsonObject data;
    private Map<String, String> header;

    public String getUrl() {
        return url;
    }

    public String getData() {
        return data.toString().trim();
    }

    public Map<String, String> getHeader() {
        try {
            long length = RequestBody.create(getData().getBytes(StandardCharsets.UTF_8), MediaType.parse("application/json")).contentLength();
            header.put("Content-Length", String.valueOf(length));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return header;
    }
    public RequestProvider create(MainPanel mainPanel, String question) {
        myProject = mainPanel.getProject();
        RequestProvider provider = new RequestProvider();

        provider.url = FLOW_COMPLETIONS_API;
        provider.header = TokenManager.getInstance().getFlowHeaders();
        provider.data = OfficialBuilder.buildFlowMessage(question, mainPanel.getContentPanel());

        return provider;
    }
}
