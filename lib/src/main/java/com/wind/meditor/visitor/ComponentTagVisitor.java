package com.wind.meditor.visitor;

import com.wind.meditor.property.ModificationProperty;
import com.wind.meditor.utils.NodeValue;

import pxb.android.axml.NodeVisitor;

/**
 * @author liangxiwei
 * @version 1.0
 * @since 2020/10/15
 */
public class ComponentTagVisitor extends NodeVisitor {
    private ModificationProperty properties;
    private String componentName;
    public ComponentTagVisitor(String componentName, ModificationProperty propertes) {
        this.properties = propertes;
        this.componentName = componentName;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        //权限名字也要把包名换掉
        String modifyPackageName = properties.getModifyPackageName();
        String originPackageName = properties.getOriginPackageName();
        if (!originPackageName.equals(modifyPackageName) && type == TYPE_STRING && NodeValue.Application.Component.PERMISSION.equals(name)) {
            String permission = ((String) obj);
            obj = permission.replaceFirst(originPackageName, modifyPackageName);
        }
        super.attr(ns, name, resourceId, type, obj);
    }
}
