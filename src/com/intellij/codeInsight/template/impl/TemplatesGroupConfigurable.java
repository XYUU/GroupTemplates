package com.intellij.codeInsight.template.impl;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by XYUU on 2016/10/28.
 */
public class TemplatesGroupConfigurable extends BaseConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    static final String ID = "editing.templatesGroup";
    private TemplatesGroupListPanel myPanel;

    @Override
    public boolean isModified() {
        return myPanel != null && myPanel.isModified();
    }

    @Override
    public JComponent createComponent() {
        myPanel = new TemplatesGroupListPanel();
        return myPanel;
    }

    @Override
    public String getDisplayName() {
        return TemplatesGroupBundle.message("title.file.templatesGroup");
    }

    @Override
    public void reset() {
        myPanel.reset();
    }

    @Override
    public void apply() throws ConfigurationException {
        myPanel.apply();
    }

    @Override
    public void disposeUIResources() {
        if (myPanel != null) {
            Disposer.dispose(myPanel);
        }
        myPanel = null;
    }

    @Override
    @NotNull
    public String getHelpTopic() {
        return ID;
    }

    @Override
    @NotNull
    public String getId() {
        return getHelpTopic();
    }

    @Override
    @Nullable
    public Runnable enableSearch(final String option) {
        return () -> myPanel.selectNode(option);
    }

    public TemplatesGroupListPanel getTemplatesGroupListPanel() {
        return myPanel;
    }
}
