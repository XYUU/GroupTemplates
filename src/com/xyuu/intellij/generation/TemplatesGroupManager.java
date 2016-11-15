package com.xyuu.intellij.generation;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.xyuu.intellij.GroupTemplate;
import org.jetbrains.java.generate.exception.TemplateResourceException;
import org.jetbrains.java.generate.template.TemplateResource;
import org.jetbrains.java.generate.template.TemplatesManager;

import java.io.IOException;

/**
 * Created by XYUU on 2016/11/12.
 */
@State(name = "TemplatesGroup", storages = @Storage("templatesGroup.xml"))
public class TemplatesGroupManager  extends TemplatesManager {

    private static final String DEFAULT = "templatesGroup.vm";

    public static TemplatesGroupManager getInstance() {
        return ServiceManager.getService(TemplatesGroupManager.class);
    }

    @Override
    public TemplateResource[] getDefaultTemplates() {
        try {
            return new TemplateResource[]{
                    new GroupTemplate("IntelliJ Default", readFile(DEFAULT),null, "vm",true)
            };
        }
        catch (IOException e) {
            throw new TemplateResourceException("Error loading default templates", e);
        }
    }

    protected static String readFile(String resource) throws IOException {
        return readFile(resource, TemplatesGroupManager.class);
    }
}
