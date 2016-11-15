package com.xyuu.intellij;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import org.apache.velocity.runtime.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.generate.template.TemplateResource;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by XYUU on 2016/11/15.
 */
public class GroupTemplate extends TemplateResource implements FileTemplate, Cloneable {
    private String description;
    private boolean reformatCode;
    private boolean liveTemplateEnabled;
    private String extension;

    public GroupTemplate(String name, String text, String description,String extension, boolean aDefault) {
        super(name, text, aDefault);
        this.description = description;
        this.extension = extension;
    }

    @NotNull
    @Override
    public String getName() {
        return getFileName();
    }

    @Override
    public void setName(@NotNull String name) {
        setFileName(name);
    }

    @Override
    public boolean isTemplateOfType(FileType fType) {
        return false;
    }


    @NotNull
    @Override
    public String getDescription() {
        return description;
    }

    @NotNull
    @Override
    public String getText() {
        return getTemplate();
    }

    @Override
    public void setText(String text) {
        setTemplate(text);
    }

    @NotNull
    @Override
    public String getText(Map attributes) throws IOException {
        return FileTemplateUtil.mergeTemplate(attributes, getText(), false);
    }

    @NotNull
    @Override
    public String getText(Properties attributes) throws IOException {
        return FileTemplateUtil.mergeTemplate(attributes, getText(), false);
    }

    @NotNull
    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public void setExtension(@NotNull String extension) {
        this.extension = extension;
    }

    @Override
    public boolean isReformatCode() {
        return reformatCode;
    }

    @Override
    public void setReformatCode(boolean reformat) {
        reformatCode = reformat;
    }

    @Override
    public boolean isLiveTemplateEnabled() {
        return liveTemplateEnabled;
    }

    @Override
    public void setLiveTemplateEnabled(boolean value) {
        liveTemplateEnabled = value;
    }

    @Override
    public GroupTemplate clone() {
        try {
            return (GroupTemplate) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public String[] getUnsetAttributes(@NotNull Properties properties, Project project) throws ParseException {
        return FileTemplateUtil.calculateAttributes(getText(), properties, false, project);
    }
}
