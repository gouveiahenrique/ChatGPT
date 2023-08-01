package com.ciandt.flow.auth;

import com.ciandt.flow.GPT35TurboHandler;
import com.ciandt.flow.settings.OpenAISettingsState;
import com.google.gson.JsonObject;
import com.ciandt.flow.settings.GPT3_35_TurboPanel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.google.gson.JsonParser.parseString;

public class FlowAuthHandler {
    private static final String API_URL = "http://localhost:22450/api/flow/login";
    private static final Logger LOG = LoggerFactory.getLogger(GPT35TurboHandler.class);
    public static void authenticate(GPT3_35_TurboPanel gpt335TurboPanel) {
        try {
            Runtime runtime = Runtime.getRuntime();
            String[] installCommand = {"flow-auth"};
            Process process = runtime.exec(installCommand);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            while (!bufferedReader.readLine().equals("Server running on port 22450")) {
                System.out.println("Looking for a bufferedReader result");
            }

            String token = auth();

            if (token.isEmpty()) {
                throw new Exception("JWT can't be null.");
            }

            gpt335TurboPanel.setApiKey(token);
            gpt335TurboPanel.apply();
        } catch (Exception e) {
            LOG.error("EXCEPTION: " + e);
        }
    }
    private static String auth() {
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        int readTimeout = Integer.parseInt(instance.readTimeout);
        int connectionTimeout = Integer.parseInt(instance.connectionTimeout);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        Request request = new Request.Builder()
                .url(API_URL)
                .get()
                .build();
        OkHttpClient httpClient = builder
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connectionTimeout, TimeUnit.MILLISECONDS)
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            JsonObject object = parseString(response.body().string()).getAsJsonObject();
            return String.valueOf(object.get("token"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
