package com.xyuu.intellij.generation;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.util.Consumer;
import com.intellij.util.containers.ContainerUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by XYUU on 2016/11/14.
 */
public class TemplatesGroupEngine {
    private static final Logger LOG = Logger.getInstance("#com.xyuu.intellij.generation.TemplatesGroupEngine");

    private VelocityContext context;
    private boolean useSystemLineSeparators;
    private Project project;

    public TemplatesGroupEngine(Project project, boolean useSystemLineSeparators) {
        this.useSystemLineSeparators = useSystemLineSeparators;
        this.project = project;
        context = new VelocityContext();
        context.put("StringUtils", StringUtils.class);
        context.put("engine",this);
    }

    public void evalFileTemplate(String templateName,String output){
        FileTemplate template = FileTemplateManager.getInstance(project).getTemplate(templateName);
        String templateContent = template.getText();
        try {
            String content = mergeTemplate(templateContent,null);
            System.out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String mergeTemplate(String templateContent, @Nullable Consumer<VelocityException> exceptionHandler) throws IOException {
        final StringWriter stringWriter = new StringWriter();
        try {
            Project project = null;
            final Object projectName = context.get(FileTemplateManager.PROJECT_NAME_VARIABLE);
            if (projectName instanceof String) {
                Project[] projects = ProjectManager.getInstance().getOpenProjects();
                project = ContainerUtil.find(projects, project1 -> projectName.equals(project1.getName()));
            }
            Velocity.evaluate(context, stringWriter, "",templateContent);
        }
        catch (final VelocityException e) {
            if (ApplicationManager.getApplication().isUnitTestMode()) {
                LOG.error(e);
            }
            LOG.info("Error evaluating template:\n" + templateContent, e);
            if (exceptionHandler == null) {
                ApplicationManager.getApplication()
                        .invokeLater(() -> Messages.showErrorDialog(IdeBundle.message("error.parsing.file.template", e.getMessage()),
                                IdeBundle.message("title.velocity.error")));
            }
            else {
                exceptionHandler.consume(e);
            }
        }
        final String result = stringWriter.toString();
        if (useSystemLineSeparators) {
            final String newSeparator = CodeStyleSettingsManager.getSettings(ProjectManagerEx.getInstanceEx().getDefaultProject()).getLineSeparator();
            if (!"\n".equals(newSeparator)) {
                return StringUtil.convertLineSeparators(result, newSeparator);
            }
        }
        return result;
    }

    public VelocityContext getContext() {
        return context;
    }

    public TemplatesGroupEngine setContext(VelocityContext context) {
        this.context = context;
        return this;
    }
}
