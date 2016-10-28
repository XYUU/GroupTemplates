package com.intellij.codeInsight.template.impl;

import com.intellij.ide.fileTemplates.FileTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by XYUU on 2016/10/28.
 */
public class FileTemplatesGroup {
    private final String name;
    private Set<FileTemplate> elements;

    public FileTemplatesGroup(final String name) {
        this.name = name;
        this.elements = new HashSet<FileTemplate>();
    }

    public Set<FileTemplate> getElements() {
        return elements;
    }
}
