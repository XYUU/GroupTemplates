package com.intellij.codeInsight.template.impl;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.*;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by XYUU on 2016/10/28.
 */
public class TemplatesGroupListPanel extends JPanel implements Disposable {

    private CheckboxTree myTree;
    private CheckedTreeNode myTreeRoot = new CheckedTreeNode(null);
    private final java.util.List<FileTemplatesGroup> myTemplateGroups = new ArrayList<FileTemplatesGroup>();

    public TemplatesGroupListPanel(){
        super(new BorderLayout());
        add(createTable(), BorderLayout.CENTER);
    }

    private JPanel createTable() {
        myTreeRoot = new CheckedTreeNode(null);
        myTree = new CheckboxTree(new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(final JTree tree,
                                          Object value,
                                          final boolean selected,
                                          final boolean expanded,
                                          final boolean leaf,
                                          final int row,
                                          final boolean hasFocus) {
                if (!(value instanceof DefaultMutableTreeNode)) return;
                value = ((DefaultMutableTreeNode)value).getUserObject();
                if (value instanceof FileTemplate) {
                    FileTemplate template = (FileTemplate)value;
                    FileTemplate defaultTemplate = FileTemplateManager.getDefaultInstance().getTemplate(template.getName());
                    Color fgColor = defaultTemplate != null ? JBColor.BLUE : null;
                    getTextRenderer().append(template.getName(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, fgColor));
                    String description = template.getDescription();
                    if (StringUtil.isNotEmpty(description)) {
                        getTextRenderer().append(" (" + description + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
                    }
                }
                else if (value instanceof FileTemplatesGroup) {
                    getTextRenderer().append(((FileTemplatesGroup)value).getName(), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
                }
            }
        }, myTreeRoot);

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
                        addTemplateOrGroup(button);
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

    private void addTemplateOrGroup(AnActionButton button) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new DumbAwareAction("File Code Template") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                SelectTemplateDialog dialog = new SelectTemplateDialog(e.getProject());
                dialog.show();
            }
        });
        group.add(new DumbAwareAction("Template Group...") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String newName = Messages.showInputDialog(myTree, "Enter the new group name:", "Create New Group", null, "", new TemplatesGroupListPanel.TemplateGroupInputValidator(null));
                if (newName != null) {
                    FileTemplatesGroup newGroup = new FileTemplatesGroup(newName);
                    setSelectedNode(insertNewGroup(newGroup));
                }
            }
        });
        DataContext context = DataManager.getInstance().getDataContext(button.getContextComponent());
        ListPopup popup = JBPopupFactory.getInstance()
                .createActionGroupPopup(null, group, context, JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING, true, null);
        popup.show(button.getPreferredPopupPoint());
    }

    @Nullable
    private FileTemplatesGroup getTemplateGroup(final String groupName) {
        for (FileTemplatesGroup group : myTemplateGroups) {
            if (group.getName().equals(groupName)) return group;
        }
        return null;
    }

    private DefaultMutableTreeNode insertNewGroup(final FileTemplatesGroup newGroup) {
        myTemplateGroups.add(newGroup);
        int index = getIndexToInsert(myTreeRoot, newGroup.getName());
        DefaultMutableTreeNode groupNode = new CheckedTreeNode(newGroup);
        myTreeRoot.insert(groupNode, index);
        ((DefaultTreeModel)myTree.getModel()).nodesWereInserted(myTreeRoot, new int[]{index});
        return groupNode;
    }

    private void setSelectedNode(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        myTree.expandPath(path.getParentPath());
        int row = myTree.getRowForPath(path);
        myTree.setSelectionRow(row);
        myTree.scrollRowToVisible(row);
    }

    private static int getIndexToInsert(DefaultMutableTreeNode parent, String key) {
        if (parent.getChildCount() == 0) return 0;
        int res = 0;
        for (DefaultMutableTreeNode child = (DefaultMutableTreeNode)parent.getFirstChild();
             child != null;
             child = (DefaultMutableTreeNode)parent.getChildAfter(child)) {
            Object o = child.getUserObject();
            String key1 = o instanceof FileTemplate ? ((FileTemplate)o).getName() : ((FileTemplatesGroup)o).getName();
            if (key1.compareToIgnoreCase(key) > 0) return res;
            res++;
        }
        return res;
    }

    private class TemplateGroupInputValidator implements InputValidator {
        private final String myOldName;

        public TemplateGroupInputValidator(String oldName) {
            myOldName = oldName;
        }

        @Override
        public boolean checkInput(String inputString) {
            return StringUtil.isNotEmpty(inputString) &&
                    (getTemplateGroup(inputString) == null || inputString.equals(myOldName));
        }

        @Override
        public boolean canClose(String inputString) {
            return checkInput(inputString);
        }
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
