package net.xyuu.intellij.extensions;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.openapi.fileTypes.StdFileTypes;
import org.jetbrains.annotations.NonNls;

/**
 * Created by XYUU on 2016/11/15.
 */
public class BatchTemplateGroupDescriptorFactory implements FileTemplateGroupDescriptorFactory {
    @NonNls
    public static final String APP_ENGINE_WEB_XML_TEMPLATE = "AppEngineWeb.xml";
    @NonNls public static final String APP_ENGINE_APPLICATION_XML_TEMPLATE = "AppEngineApplication.xml";
    @NonNls public static final String APP_ENGINE_JDO_CONFIG_TEMPLATE = "AppEngineJdoConfig.xml";
    @NonNls public static final String APP_ENGINE_JPA_CONFIG_TEMPLATE = "AppEngineJpaConfig.xml";
    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateDescriptor appEngineXml = new FileTemplateDescriptor(APP_ENGINE_WEB_XML_TEMPLATE, StdFileTypes.XML.getIcon());
        final FileTemplateDescriptor appEngineApplicationXml = new FileTemplateDescriptor(APP_ENGINE_APPLICATION_XML_TEMPLATE, StdFileTypes.XML.getIcon());
        final FileTemplateDescriptor jdoConfigXml = new FileTemplateDescriptor(APP_ENGINE_JDO_CONFIG_TEMPLATE, StdFileTypes.XML.getIcon());
        final FileTemplateDescriptor jpaConfigXml = new FileTemplateDescriptor(APP_ENGINE_JPA_CONFIG_TEMPLATE, StdFileTypes.XML.getIcon());
        return new FileTemplateGroupDescriptor("Batch Template Group", StdFileTypes.IDEA_MODULE.getIcon(), appEngineXml,
                appEngineApplicationXml, jdoConfigXml, jpaConfigXml);
    }
}
