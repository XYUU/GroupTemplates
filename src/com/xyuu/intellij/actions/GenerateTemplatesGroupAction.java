package com.xyuu.intellij.actions;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.xyuu.intellij.generation.GenerateTemplatesGroupHandler;

/**
 * Created by XYUU on 2016/11/12.
 */
public class GenerateTemplatesGroupAction extends BaseGenerateAction {
    public GenerateTemplatesGroupAction() {
        super(new GenerateTemplatesGroupHandler());
    }

}