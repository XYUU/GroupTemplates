package net.xyuu.intellij.extensions;

import com.intellij.ide.fileTemplates.DefaultTemplatePropertiesProvider;
import com.intellij.psi.PsiDirectory;

import java.util.Properties;

/**
 * Created by XYUU on 2016/11/15.
 */
public class TemplateGroupPropertiesProvider implements DefaultTemplatePropertiesProvider {
    @Override
    public void fillProperties(PsiDirectory directory, Properties props) {
        props.put("group", this);
    }


}
