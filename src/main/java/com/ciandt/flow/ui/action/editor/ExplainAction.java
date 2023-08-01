package com.ciandt.flow.ui.action.editor;

import com.ciandt.flow.message.ChatGPTBundle;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
@Deprecated
public class ExplainAction extends AbstractEditorAction {

    public ExplainAction() {
        super(() -> ChatGPTBundle.message("action.code.explain.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = "Explain this code:";
        super.actionPerformed(e);
    }

}
