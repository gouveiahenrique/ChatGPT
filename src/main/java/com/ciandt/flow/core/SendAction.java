package com.ciandt.flow.core;

import com.ciandt.flow.message.ChatGPTBundle;
import com.ciandt.flow.settings.OpenAISettingsState;
import com.ciandt.flow.util.StringUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.ciandt.flow.GPT35TurboHandler;
import com.ciandt.flow.ui.MainPanel;
import com.ciandt.flow.ui.MessageComponent;
import com.ciandt.flow.ui.MessageGroupComponent;
import okhttp3.Call;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static com.ciandt.flow.MyToolWindowFactory.ACTIVE_CONTENT;

/**
 * @author Wuzi
 */
public class SendAction extends AnAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    private String data;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Object mainPanel = project.getUserData(ACTIVE_CONTENT);
        doActionPerformed((MainPanel) mainPanel, data);
    }

    private boolean presetCheck(boolean isChatGPTModel) {
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        String errorMessage = "";
        if (isChatGPTModel) {
            if (StringUtil.isEmpty(instance.accessToken)) {
                errorMessage = "Please configure the access token first.";
            }
            if (instance.enableCustomizeChatGPTUrl && StringUtil.isEmpty(instance.customizeUrl)) {
                errorMessage = "Please configure ChatGPT customize server first.";
            }
        } else {
            if (StringUtil.isEmpty(instance.apiKey)) {
                errorMessage = "Please log in the Flow.";
            }
            if (instance.enableCustomizeGpt35TurboUrl && StringUtil.isEmpty(instance.gpt35TurboUrl)) {
                errorMessage = "Please configure GPT-3.5-Turbo customize server first.";
            }
        }

        if (!errorMessage.isEmpty()) {
            Notifications.Bus.notify(
                    new Notification(ChatGPTBundle.message("group.id"),
                            "Wrong setting",
                            errorMessage,
                            NotificationType.ERROR));
            return false;
        }

        return true;
    }

    public void doActionPerformed(MainPanel mainPanel, String data) {
        // Filter the empty text
        if (StringUtils.isEmpty(data)) {
            return;
        }

        // Check the configuration first
        if (!presetCheck(mainPanel.isChatGPTModel())) {
            return;
        }

        // Reset the question container
        mainPanel.getSearchTextArea().getTextArea().setText("");
        mainPanel.aroundRequest(true);
        Project project = mainPanel.getProject();
        MessageGroupComponent contentPanel = mainPanel.getContentPanel();

        // Add the message component to container
        MessageComponent question = new MessageComponent(data,true);
        MessageComponent answer = new MessageComponent("Waiting for response...",false);
        contentPanel.add(question);
        contentPanel.add(answer);

        try {
            ExecutorService executorService = mainPanel.getExecutorService();
            // Request the server.
            GPT35TurboHandler gpt35TurboHandler = project.getService(GPT35TurboHandler.class);
            executorService.submit(() -> {
                Call handle = gpt35TurboHandler.handle(mainPanel, answer, data);
                mainPanel.setRequestHolder(handle);
                contentPanel.updateLayout();
                contentPanel.scrollToBottom();
            });
        } catch (Exception e) {
            answer.setSourceContent(e.getMessage());
            answer.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
            LOG.error("ChatGPT: Request failed, error={}", e.getMessage());
        }
    }
}
