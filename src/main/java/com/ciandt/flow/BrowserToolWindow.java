package com.ciandt.flow;

import com.ciandt.flow.ui.BrowserContent;

import javax.swing.*;

/**
 * @author Wuzi
 */
public class BrowserToolWindow {

    private final BrowserContent content;

    public BrowserToolWindow() {
        content = new BrowserContent();
    }

    public JPanel getContent() {
        return content.getContentPanel();
    }

    public BrowserContent getPanel() {
        return content;
    }
}
