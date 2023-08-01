package com.ciandt.flow.ui;

import com.intellij.CommonBundle;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * @author Wuzi
 */
public class SupportDialog extends DialogWrapper {

    private JPanel panel;

    public SupportDialog(@Nullable Project project) {
        super(project);
        setTitle("Support / Donate");
        setResizable(false);
        setOKButtonText("Thanks for Your Supporting!");
        init();
        setOKActionEnabled(true);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        panel = new JPanel();
        panel.setLayout(new VerticalLayout(JBUIScale.scale(8)));
        panel.setBorder(JBUI.Borders.empty(20));
        panel.setBackground(UIManager.getColor("TextArea.background"));

        return panel;
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return panel;
    }

    @Override
    protected @NotNull DialogStyle getStyle() {
        return DialogStyle.COMPACT;
    }

    @Override
    protected Action @NotNull [] createActions() {
        myOKAction = new DialogWrapperAction(CommonBundle.getOkButtonText()) {
            @Override
            protected void doAction(ActionEvent e) {
                dispose();
                close(OK_EXIT_CODE);
            }
        };
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(getOKAction());
        return actions.toArray(new Action[0]);
    }
    private ActionLink createActionLink(String text, String url) {
        ActionLink actionLink = new ActionLink(text);
        actionLink.addActionListener(e -> BrowserUtil.browse(url));
        return actionLink;
    }
}
