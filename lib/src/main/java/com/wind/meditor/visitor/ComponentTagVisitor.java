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
    public ComponentTagVisitor(NodeVisitor nv, String componentName, ModificationProperty propertes) {
        super(nv);
        this.properties = propertes;
        this.componentName = componentName;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        //权限名字也要把包名换掉
        String modifyPackageName = properties.getModifyPackageName();
        String originPackageName = properties.getOriginPackageName();

        if (!originPackageName.equals(modifyPackageName) && type == TYPE_STRING ) {
            boolean isPermissionAttr = NodeValue.Application.Component.PERMISSION.equals(name) ||
                    NodeValue.Application.Component.WRITE_PERMISSION.equals(name)||
                    NodeValue.Application.Component.READ_PERMISSION.equals(name);
            boolean isAuthorities = NodeValue.Application.Component.AUTHORITIES.equals(name);
            if (isPermissionAttr || isAuthorities) {
                String permission = ((String) obj);
                obj = permission.replaceFirst(originPackageName, modifyPackageName);
            }
        }
        super.attr(ns, name, resourceId, type, obj);
    }

    @Override
    public NodeVisitor child(String ns, String name) {

        return super.child(ns, name);
    }
}
