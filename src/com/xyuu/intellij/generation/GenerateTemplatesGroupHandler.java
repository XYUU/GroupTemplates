package com.xyuu.intellij.generation;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.CodeInsightUtilBase;
import com.intellij.codeInsight.generation.*;
import com.intellij.codeInsight.template.impl.TemplatesGroupBundle;
import com.intellij.ide.util.MemberChooser;
import com.intellij.lang.ContextAwareActionHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocCommentOwner;
import com.intellij.psi.PsiFile;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.util.ui.UIUtil;
import org.apache.velocity.VelocityContext;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.generate.exception.GenerateCodeException;
import org.jetbrains.java.generate.template.TemplateResource;
import org.jetbrains.java.generate.template.TemplatesManager;
import org.jetbrains.java.generate.view.TemplatesPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by XYUU on 2016/11/12.
 */
public class GenerateTemplatesGroupHandler extends GenerateAccessorProviderRegistrar implements CodeInsightActionHandler, ContextAwareActionHandler {

    private static final Logger LOG = Logger.getInstance("#com.intellij.codeInsight.generation.GenerateTemplatesGroupHandler");

    protected boolean myToCopyJavaDoc;

    @Nullable
    protected JComponent getHeaderPanel(Project project) {
        final JPanel panel = new JPanel(new BorderLayout(2, 1));
        panel.add(getHeaderPanel(project, TemplatesGroupManager.getInstance(), TemplatesGroupBundle.message("title.file.templatesGroup")), BorderLayout.NORTH);
        return panel;
    }

    protected MemberChooser<ClassMember> createMembersChooser(ClassMember[] members,
                                                              boolean allowEmptySelection,
                                                              boolean copyJavadocCheckbox,
                                                              Project project) {
        MemberChooser<ClassMember> chooser = new MemberChooser<ClassMember>(members, allowEmptySelection, true, project, getHeaderPanel(project), null) {
            @Nullable
            @Override
            protected String getHelpId() {
                return "Getter and Setter Templates Dialog";
            }
        };
        chooser.setTitle(TemplatesGroupBundle.message("title.file.templatesGroup"));
        chooser.setCopyJavadocVisible(copyJavadocCheckbox);
        return chooser;
    }

    protected static JComponent getHeaderPanel(final Project project, final TemplatesManager templatesManager, final String templatesTitle) {
        final JPanel panel = new JPanel(new BorderLayout());
        final JLabel templateChooserLabel = new JLabel(templatesTitle);
        panel.add(templateChooserLabel, BorderLayout.WEST);
        final ComboBox comboBox = new ComboBox();
        templateChooserLabel.setLabelFor(comboBox);
        comboBox.setRenderer(new ListCellRendererWrapper<TemplateResource>() {
            @Override
            public void customize(JList list, TemplateResource value, int index, boolean selected, boolean hasFocus) {
                setText(value.getName());
            }
        });
        final ComponentWithBrowseButton<ComboBox> comboBoxWithBrowseButton =
                new ComponentWithBrowseButton<ComboBox>(comboBox, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final TemplatesPanel ui = new TemplatesPanel(project, templatesManager) {
                            @Override
                            protected boolean onMultipleFields() {
                                return false;
                            }

                            @Nls
                            @Override
                            public String getDisplayName() {
                                return StringUtil.capitalizeWords(UIUtil.removeMnemonic(StringUtil.trimEnd(templatesTitle, ":")), true);
                            }
                        };
                        ui.setHint("Visibility is applied according to File | Settings | Editor | Code Style | Java | Code Generation");
                        ui.selectNodeInTree(templatesManager.getDefaultTemplate());
                        if (ShowSettingsUtil.getInstance().editConfigurable(panel, ui)) {
                            setComboboxModel(templatesManager, comboBox);
                        }
                    }
                });

        setComboboxModel(templatesManager, comboBox);
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(@NotNull final ActionEvent M) {
                templatesManager.setDefaultTemplate((TemplateResource) comboBox.getSelectedItem());
            }
        });
        panel.add(comboBoxWithBrowseButton, BorderLayout.CENTER);
        return panel;
    }

    private static void setComboboxModel(TemplatesManager templatesManager, ComboBox comboBox) {
        final Collection<TemplateResource> templates = templatesManager.getAllTemplates();
        comboBox.setModel(new DefaultComboBoxModel(templates.toArray(new TemplateResource[templates.size()])));
        comboBox.setSelectedItem(templatesManager.getDefaultTemplate());
    }


    @Nullable
    protected ClassMember[] chooseOriginalMembers(PsiClass aClass, Project project) {
        ClassMember[] allMembers = getAllOriginalMembers(aClass);
        return chooseMembers(allMembers, false, false, project, null);
    }

    @Nullable
    protected ClassMember[] chooseOriginalMembers(PsiClass aClass, Project project, Editor editor) {
        return chooseOriginalMembers(aClass, project);
    }

    @Nullable
    protected ClassMember[] chooseMembers(ClassMember[] members,
                                          boolean allowEmptySelection,
                                          boolean copyJavadocCheckbox,
                                          Project project,
                                          @Nullable Editor editor) {
        MemberChooser<ClassMember> chooser = createMembersChooser(members, allowEmptySelection, copyJavadocCheckbox, project);
        if (editor != null) {
            final int offset = editor.getCaretModel().getOffset();

            ClassMember preselection = null;
            for (ClassMember member : members) {
                if (member instanceof PsiElementClassMember) {
                    final PsiDocCommentOwner owner = ((PsiElementClassMember) member).getElement();
                    if (owner != null) {
                        final TextRange textRange = owner.getTextRange();
                        if (textRange != null && textRange.contains(offset)) {
                            preselection = member;
                            break;
                        }
                    }
                }
            }
            if (preselection != null) {
                chooser.selectElements(new ClassMember[]{preselection});
            }
        }
        chooser.show();
        myToCopyJavaDoc = chooser.isCopyJavadoc();
        final java.util.List<ClassMember> list = chooser.getSelectedElements();
        return list == null ? null : list.toArray(new ClassMember[list.size()]);
    }

    @Nullable
    protected ClassMember[] getAllOriginalMembers(final PsiClass aClass) {
        java.util.List<EncapsulatableClassMember> members = getEncapsulatableClassMembers(aClass);
        return members.toArray(new ClassMember[members.size()]);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (!CodeInsightUtilBase.prepareEditorForWrite(editor)) return;
        if (!FileDocumentManager.getInstance().requestWriting(editor.getDocument(), project)) {
            return;
        }
        final PsiClass aClass = OverrideImplementUtil.getContextClass(project, editor, file, false);
        if (aClass == null || aClass.isInterface()) return; //?
        LOG.assertTrue(aClass.isValid());
        LOG.assertTrue(aClass.getContainingFile() != null);
        try {
            final ClassMember[] members = chooseOriginalMembers(aClass, project, editor);
            if (members == null) return;
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    doGenerate(project,aClass, members);
                }
                catch (GenerateCodeException e) {
                    final String message = e.getMessage();
                    ApplicationManager.getApplication().invokeLater(() -> {

                    }, project.getDisposed());
                }
            });
        }
        finally {
            cleanup();
        }
    }

    private void doGenerate(Project project, PsiClass aClass, ClassMember[] members) {
        String template = TemplatesGroupManager.getInstance().getDefaultTemplate().getTemplate();
        TemplatesGroupEngine engine = new TemplatesGroupEngine(project,false);
        try {
            VelocityContext context = engine.getContext();
            context.put("entity",aClass);
            context.put("members",members);
            engine.mergeTemplate(template,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void cleanup() {

    }

    @Override
    public boolean startInWriteAction() {
        return false;
    }

    @Override
    public boolean isAvailableForQuickList(@NotNull Editor editor, @NotNull PsiFile file, @NotNull DataContext dataContext) {
        final PsiClass aClass = OverrideImplementUtil.getContextClass(file.getProject(), editor, file, false);
        return aClass != null;
    }
}
