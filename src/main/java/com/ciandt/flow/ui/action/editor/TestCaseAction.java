package com.ciandt.flow.ui.action.editor;

import com.ciandt.flow.message.ChatGPTBundle;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
@Deprecated
public class TestCaseAction extends AbstractEditorAction {

    public TestCaseAction() {
        super(() -> ChatGPTBundle.message("action.code.test.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = "Add test case for code:";
        super.actionPerformed(e);
    }

}
