package net.xyuu.intellij.extensions;

import com.intellij.ide.fileTemplates.CreateFromTemplateHandler;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created by XYUU on 2016/11/15.
 */
public class GroupCreateFromTemplateHandler implements CreateFromTemplateHandler {
    @Override
    public boolean handlesTemplate(FileTemplate template) {
        return false;
    }

    @NotNull
    @Override
    public PsiElement createFromTemplate(Project project, PsiDirectory directory, String fileName, FileTemplate template, String templateText, @NotNull Map<String, Object> props) throws IncorrectOperationException {

        return null;
    }

    @Override
    public boolean canCreate(PsiDirectory[] dirs) {
        return true;
    }

    @Override
    public boolean isNameRequired() {
        return false;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public void prepareProperties(Map<String, Object> props) {

    }
}
