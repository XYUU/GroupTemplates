package com.intellij.codeInsight.template.impl;



import com.intellij.ide.IdeBundle;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.fileTemplates.ui.ConfigureTemplatesDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.FixedSizeButton;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Created by XYUU on 2016/10/29.
 */
public class SelectTemplateDialog extends DialogWrapper{
    private JComboBox myCbxTemplates;
    private FileTemplate mySelectedTemplate;
    private final Project myProject;

    public SelectTemplateDialog(Project project){
        super(project, true);
        myProject = project;
        setTitle(IdeBundle.message("title.select.template"));
        init();
    }

    @Override
    protected JComponent createCenterPanel(){
        loadCombo();

        JButton editTemplatesButton = new FixedSizeButton(myCbxTemplates);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        JLabel selectTemplateLabel = new JLabel(IdeBundle.message("label.name"));

        centerPanel.add(selectTemplateLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, JBUI.insets(2), 0, 0));
        centerPanel.add(myCbxTemplates, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, JBUI.insets(2), 50, 0));
        centerPanel.add(editTemplatesButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, JBUI.insets(2), 0, 0));

        editTemplatesButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                onEditTemplates();
            }
        });

        return centerPanel;
    }

    private void loadCombo(){
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        FileTemplate[] allTemplates = FileTemplateManager.getInstance(myProject).getAllTemplates();
        for (FileTemplate template : allTemplates) {
                model.addElement(template);
        }
        if(myCbxTemplates == null){
            myCbxTemplates = new JComboBox(model);
            myCbxTemplates.setRenderer(new ListCellRendererWrapper<FileTemplate>() {
                @Override
                public void customize(JList list, FileTemplate fileTemplate, int index, boolean selected, boolean hasFocus) {
                    if (fileTemplate != null) {
                        setIcon(FileTemplateUtil.getIcon(fileTemplate));
                        setText(fileTemplate.getName());
                    }
                }
            });
        }
        else{
            Object selected = myCbxTemplates.getSelectedItem();
            myCbxTemplates.setModel(model);
            if(selected != null){
                myCbxTemplates.setSelectedItem(selected);
            }
        }
    }

    public FileTemplate getSelectedTemplate(){
        return mySelectedTemplate;
    }

    @Override
    protected void doOKAction(){
        mySelectedTemplate = (FileTemplate)myCbxTemplates.getSelectedItem();
        super.doOKAction();
    }

    @Override
    public void doCancelAction(){
        mySelectedTemplate = null;
        super.doCancelAction();
    }

    @Override
    public JComponent getPreferredFocusedComponent(){
        return myCbxTemplates;
    }

    private void onEditTemplates(){
        new ConfigureTemplatesDialog(myProject).show();
        loadCombo();
    }

}
