package com.ciandt.flow.ui.action.editor;

import com.ciandt.flow.message.ChatGPTBundle;
import com.ciandt.flow.settings.OpenAISettingsState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
@Deprecated
public class OptimizeAction extends AbstractEditorAction {

    public OptimizeAction() {
        super(() -> ChatGPTBundle.message("action.code.optimize.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt2Name;
        super.actionPerformed(e);
    }

}
