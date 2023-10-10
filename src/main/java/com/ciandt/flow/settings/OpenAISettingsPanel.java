// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.ciandt.flow.settings;

import com.ciandt.flow.message.ChatGPTBundle;
import com.ciandt.flow.util.OpenAIUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Wuzi
 */
public class OpenAISettingsPanel implements Configurable, Disposable {
    private JPanel myMainPanel;
    private JPanel connectionTitledBorderBox;
    private JBTextField readTimeoutField;
    private JBTextField connectionTimeoutField;
    private JPanel proxyTitledBorderBox;
    private JCheckBox enableAvatarCheckBox;
    private JPanel contentTitledBorderBox;
    private JComboBox<String> firstCombobox;
    private JComboBox<String> secondCombobox;
    private JComboBox<String> thirdCombobox;
    private JCheckBox enableLineWarpCheckBox;
    private JLabel readTimeoutHelpLabel;
    private JLabel connectionTimeoutHelpLabel;
    private JLabel contentOrderHelpLabel;
    private JPanel openaiAssistantTitledBorderBox;
    private JPanel corePromptBorderBox;
    private JBTextField prompt1NameField;
    private JBTextField prompt2NameField;
    private JBTextField prompt3NameField;
    private ExpandableTextField prompt1ValueField;
    private ExpandableTextField prompt3ValueField;
    private ExpandableTextField prompt2ValueField;
    private boolean needRestart = false;

    public static final String FIND_GRANTS = "https://api.openai.com/dashboard/billing/credit_grants";
    public static final String CREATE_API_KEY = "https://api.openai.com/dashboard/user/api_keys";

    public OpenAISettingsPanel() {
        init();
    }

    private void init() {
        readTimeoutField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.connection.read_timeout.empty_text"));
        connectionTimeoutField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.connection.connection_timeout.empty_text"));

        prompt1ValueField.setFont(JBUI.Fonts.label());
        prompt2ValueField.setFont(JBUI.Fonts.label());
        prompt3ValueField.setFont(JBUI.Fonts.label());
    }
    @Override
    public void reset() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        readTimeoutField.setText(state.readTimeout);
        connectionTimeoutField.setText(state.connectionTimeout);

        enableAvatarCheckBox.setSelected(state.enableAvatar);

        enableLineWarpCheckBox.setSelected(state.enableLineWarp);

        prompt1NameField.setText(state.prompt1Name);
        prompt1ValueField.setText(state.prompt1Value);
        prompt2NameField.setText(state.prompt2Name);
        prompt2ValueField.setText(state.prompt2Value);
        prompt3NameField.setText(state.prompt3Name);
        prompt3ValueField.setText(state.prompt3Value);

        initHelp();
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        // If you change the order, you need to restart the IDE to take effect
        needRestart = !state.enableLineWarp == enableLineWarpCheckBox.isSelected() ||
                !StringUtil.equals(state.prompt1Name, prompt1NameField.getText()) ||
                !StringUtil.equals(state.prompt2Name, prompt2NameField.getText()) ||
                !StringUtil.equals(state.prompt3Name, prompt3NameField.getText())
        ;

        return
                !StringUtil.equals(state.readTimeout, readTimeoutField.getText()) ||
                !StringUtil.equals(state.connectionTimeout, connectionTimeoutField.getText()) ||
                !state.enableAvatar == enableAvatarCheckBox.isSelected() ||
                !state.enableLineWarp == enableLineWarpCheckBox.isSelected() ||
                !StringUtil.equals(state.prompt1Name, prompt1NameField.getText()) ||
                !StringUtil.equals(state.prompt1Value, prompt1ValueField.getText()) ||
                !StringUtil.equals(state.prompt2Name, prompt2NameField.getText()) ||
                !StringUtil.equals(state.prompt2Value, prompt2ValueField.getText()) ||
                !StringUtil.equals(state.prompt3Name, prompt3NameField.getText()) ||
                !StringUtil.equals(state.prompt3Value, prompt3ValueField.getText())
                ;
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        boolean readTimeoutIsNumber = com.ciandt.flow.util.StringUtil.isNumber(readTimeoutField.getText());
        boolean connectionTimeoutIsNumber = com.ciandt.flow.util.StringUtil.isNumber(connectionTimeoutField.getText());
        state.readTimeout = !readTimeoutIsNumber ? "50000" : readTimeoutField.getText();
        state.connectionTimeout = !connectionTimeoutIsNumber ? "50000" : connectionTimeoutField.getText();

        state.enableAvatar = enableAvatarCheckBox.isSelected();

        state.prompt1Name = prompt1NameField.getText();
        state.prompt1Value = prompt1ValueField.getText();
        state.prompt2Name = prompt2NameField.getText();
        state.prompt2Value = prompt2ValueField.getText();
        state.prompt3Name = prompt3NameField.getText();
        state.prompt3Value = prompt3ValueField.getText();

        String firstSelected = (String) firstCombobox.getSelectedItem();
        String secondSelected = (String) secondCombobox.getSelectedItem();
        String thirdSelected = (String) thirdCombobox.getSelectedItem();

        // Determine whether each location has a different Content
        List<String> strings = new ArrayList<>(3);
        strings.add(firstSelected);
        strings.add(secondSelected);
        strings.add(thirdSelected);
        List<String> collect = strings.stream().distinct().collect(Collectors.toList());
        if (collect.size() != strings.size()) {
            MessageDialogBuilder.yesNo("Duplicate Content exists!", "The content of " +
                            "each position must be unique, please re-adjust the order")
                    .yesText("Ok")
                    .noText("Close").ask(myMainPanel);
            return;
        }

        state.enableLineWarp = enableLineWarpCheckBox.isSelected();

        if (needRestart) {
            boolean yes = MessageDialogBuilder.yesNo("Content order changed!", "Changing " +
                            "the content order requires restarting the IDE to take effect. Do you " +
                            "want to restart to apply the settings?")
                    .yesText("Restart")
                    .noText("Not Now").ask(myMainPanel);
            if (yes) {
                ApplicationManagerEx.getApplicationEx().restart(true);
            }
        }
    }

    @Override
    public void dispose() {
    }

    private static void register(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingProxyType value) {
        choice.putClientProperty("value", value);
    }

    private static void setSelected(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingProxyType value) {
        choice.setSelected(value.equals(choice.getClientProperty("value")));
    }

    @Override
    public String getDisplayName() {
        return ChatGPTBundle.message("ui.setting.menu.text");
    }

    private void createUIComponents() {
        connectionTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsConnection = new TitledSeparator(ChatGPTBundle.message("ui.setting.connection.title"));
        connectionTitledBorderBox.add(tsConnection,BorderLayout.CENTER);

        proxyTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsProxy = new TitledSeparator("Proxy Settings");
        proxyTitledBorderBox.add(tsProxy,BorderLayout.CENTER);

        contentTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator("Tool Window Settings");
        contentTitledBorderBox.add(tsUrl,BorderLayout.CENTER);

        openaiAssistantTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator oaUrl = new TitledSeparator("OpenAI Assistant");
        openaiAssistantTitledBorderBox.add(oaUrl,BorderLayout.CENTER);

        corePromptBorderBox = new JPanel(new BorderLayout());
        TitledSeparator corePrompt = new TitledSeparator("Custom Built-in Prompt");
        corePromptBorderBox.add(corePrompt,BorderLayout.CENTER);
    }

    public void initHelp() {
        readTimeoutHelpLabel.setFont(JBUI.Fonts.smallFont());
        readTimeoutHelpLabel.setForeground(UIUtil.getContextHelpForeground());

        connectionTimeoutHelpLabel.setFont(JBUI.Fonts.smallFont());
        connectionTimeoutHelpLabel.setForeground(UIUtil.getContextHelpForeground());

        contentOrderHelpLabel.setFont(JBUI.Fonts.smallFont());
        contentOrderHelpLabel.setForeground(UIUtil.getContextHelpForeground());
    }
}
