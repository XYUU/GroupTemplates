package com.intellij.codeInsight.template.impl;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.*;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

/**
 * Created by XYUU on 2016/10/28.
 */
public class TemplatesGroupListPanel extends JPanel implements Disposable {

    private CheckboxTree myTree;
    private CheckedTreeNode myTreeRoot = new CheckedTreeNode(null);

    public TemplatesGroupListPanel(){
        super(new BorderLayout());
        add(createTable(), BorderLayout.CENTER);
    }

    private JPanel createTable() {
        myTreeRoot = new CheckedTreeNode(null);
        myTree = new CheckboxTree();
        myTree.setRootVisible(false);
        myTree.setShowsRootHandles(true);
        myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        myTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener(){
            @Override
            public void valueChanged(@NotNull final TreeSelectionEvent e) {

            }
        });

        return initToolbar().createPanel();

    }

    private ToolbarDecorator initToolbar() {
        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(myTree)
                .setAddAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        //addTemplateOrGroup(button);
                    }
                })
                .setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton anActionButton) {
                        //removeRows();
                    }
                })
                .disableDownAction()
                .disableUpAction()
                .addExtraAction(new AnActionButton("Duplicate", PlatformIcons.COPY_ICON) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        //copyRow();
                    }

                    @Override
                    public void updateButton(AnActionEvent e) {
                        //e.getPresentation().setEnabled(getTemplate(getSingleSelectedIndex()) != null);
                    }
                }).addExtraAction(new AnActionButton("Restore deleted defaults", AllIcons.General.TodoDefault) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        TemplateSettings.getInstance().reset();
                        reset();
                    }

                    @Override
                    public boolean isEnabled() {
                        return super.isEnabled() && !TemplateSettings.getInstance().getDeletedTemplates().isEmpty();
                    }
                });
        return decorator.setToolbarPosition(ActionToolbarPosition.RIGHT);
    }

    private boolean modified;

    public boolean isModified() {
        return modified;
    }

    public void reset() {

    }

    public void apply() {

    }

    public void selectNode(String option) {

    }

    @Override
    public void dispose() {

    }
}
