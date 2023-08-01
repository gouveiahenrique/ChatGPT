package com.ciandt.flow;

import com.ciandt.flow.ui.MainPanel;
import com.ciandt.flow.util.MyUIUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


/**
 * @author Wuzi
 */
public class GPT35TurboToolWindow {

  private final MainPanel panel;

  public GPT35TurboToolWindow(@NotNull Project project) {
    panel = new MainPanel(project, false);
  }

  public JPanel getContent() {
    return panel.init();
  }

  public MainPanel getPanel() {
    return panel;
  }

/**
 * rapidly get input focus by keystorke f key
 */
  public void registerKeystrokeFocus(){
    MyUIUtil.registerKeystrokeFocusForInput(panel.getSearchTextArea().getTextArea());
  }
}
