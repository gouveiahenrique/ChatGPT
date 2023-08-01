package com.ciandt.flow;

import com.ciandt.flow.message.ChatGPTBundle;
import com.ciandt.flow.ui.action.SettingAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Wuzi
 */
public class MyToolWindowFactory implements ToolWindowFactory {

    public static final Key ACTIVE_CONTENT = Key.create("ActiveContent");
    public static final String GPT35_TRUBO_CONTENT_NAME = "ChatWithCode";

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();

        GPT35TurboToolWindow gpt35TurboToolWindow = new GPT35TurboToolWindow(project);
        Content gpt35Turbo = contentFactory.createContent(gpt35TurboToolWindow.getContent(), GPT35_TRUBO_CONTENT_NAME, false);
        gpt35Turbo.setCloseable(false);

        toolWindow.getContentManager().addContent(gpt35Turbo);

        // Set the default component. It require the 1st container
        project.putUserData(ACTIVE_CONTENT, gpt35TurboToolWindow.getPanel());

        List<AnAction> actionList = new ArrayList<>();
        actionList.add(new SettingAction(ChatGPTBundle.message("action.settings")));
        toolWindow.setTitleActions(actionList);
    }
}
