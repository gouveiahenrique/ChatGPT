package com.ciandt.flow;

import com.ciandt.flow.core.builder.OfficialBuilder;
import com.ciandt.flow.core.parser.OfficialParser;
import com.ciandt.flow.settings.OpenAISettingsState;
import com.ciandt.flow.ui.MainPanel;
import com.ciandt.flow.ui.MessageComponent;
import com.ciandt.flow.ui.MessageGroupComponent;
import com.ciandt.flow.util.StringUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Wuzi
 */
public class GPT35TurboHandler extends AbstractHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GPT35TurboHandler.class);

    public Call handle(MainPanel mainPanel, MessageComponent component, String question) {
        MessageGroupComponent contentPanel = mainPanel.getContentPanel();

        // Define the default system role
        if (contentPanel.getMessages().isEmpty()) {
            String text = contentPanel.getSystemRole();
            contentPanel.getMessages().add(OfficialBuilder.systemMessage(text));
        }

        Call call = null;
        RequestProvider provider = new RequestProvider().create(mainPanel, question);
        try {
            RequestBody body = RequestBody.create(provider.getData().getBytes(StandardCharsets.UTF_8),
                    MediaType.parse("application/json"));
            LOG.info("Flow Request: question={}",question);
            Map<String, String> headers = provider.getHeader();
            Request request = new Request.Builder()
                    .url(provider.getUrl())
                    .headers(Headers.of(headers))
                    .post(body)
                    .build();
            OpenAISettingsState instance = OpenAISettingsState.getInstance();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(Integer.parseInt(instance.connectionTimeout), TimeUnit.MILLISECONDS)
                    .readTimeout(Integer.parseInt(instance.readTimeout), TimeUnit.MILLISECONDS);
            builder.hostnameVerifier(getHostNameVerifier());
            builder.sslSocketFactory(getSslContext().getSocketFactory(), (X509TrustManager) getTrustAllManager());
            OkHttpClient httpClient = builder.build();
            call = httpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    String errorMessage = StringUtil.isEmpty(e.getMessage())? "None" : e.getMessage();
                    if (e instanceof SocketException) {
                        LOG.info("Flow: Stop generating");
                        component.setContent("Stop generating");
                        e.printStackTrace();
                        return;
                    }
                    LOG.error("GPT 3.5 Turbo Request failure. Url={}, error={}",
                            call.request().url(),
                            errorMessage);
                    errorMessage = "GPT 3.5 Turbo Request failure, cause: " + errorMessage;
                    component.setSourceContent(errorMessage);
                    component.setContent(errorMessage);
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                    mainPanel.getExecutorService().shutdown();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseMessage = response.body().string();
                    LOG.info("Flow Response: answer={}",responseMessage);
                    if (response.code() != 200) {
                        LOG.info("Flow: Request failure. Url={}, response={}",provider.getUrl(), responseMessage);
                        component.setContent("Response failure, please try again. Error message: " + responseMessage);
                        mainPanel.aroundRequest(false);
                        return;
                    }
                    OfficialParser.ParseResult parseResult = OfficialParser.
                            parserBeesAI(responseMessage);

                    mainPanel.getContentPanel().getMessages().add(OfficialBuilder.assistantMessage(parseResult.getSource()));
                    component.setSourceContent(parseResult.getSource());
                    component.setContent(parseResult.getHtml());
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                }
            });
        } catch (Exception e) {
            component.setSourceContent(e.getMessage());
            component.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
        } finally {
            mainPanel.getExecutorService().shutdown();
        }

        return call;
    }
}
