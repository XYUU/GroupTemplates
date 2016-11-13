package com.intellij.codeInsight.template.impl;

import com.intellij.ide.fileTemplates.FileTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XYUU on 2016/10/28.
 */
public class FileTemplatesGroup {

    private final String name;
    private List<FileTemplate> elements;

    public FileTemplatesGroup(final String name) {
        this.name = name;
        this.elements = new ArrayList<FileTemplate>();
    }

    public String getName() {
        return name;
    }

    public List<FileTemplate> getElements() {
        return elements;
    }

}
