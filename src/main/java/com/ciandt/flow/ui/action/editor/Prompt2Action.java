package com.ciandt.flow.ui.action.editor;

import com.ciandt.flow.settings.OpenAISettingsState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class Prompt2Action extends AbstractEditorAction {

    public Prompt2Action() {
        super(() -> OpenAISettingsState.getInstance().prompt2Name);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        key = state.prompt2Value;
        super.actionPerformed(e);
    }

}
