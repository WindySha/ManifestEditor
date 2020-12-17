package com.wind.meditor.visitor;

import com.wind.meditor.property.ModificationProperty;

import pxb.android.axml.NodeVisitor;

/**
 * @author liangxiwei
 * @since 2020/10/16
 */
public class PermissionTagVisitor extends NodeVisitor {
    private ModificationProperty properties;
    PermissionTagVisitor(NodeVisitor nv, ModificationProperty properties) {
        super(nv);
        this.properties = properties;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        //权限名字也要把包名换掉
        String modifyPackageName = properties.getModifyPackageName();
        String originPackageName = properties.getOriginPackageName();
        if (!originPackageName.equals(modifyPackageName) && type == TYPE_STRING) {
            String permission = ((String)obj);
            obj = permission.replaceFirst(originPackageName, modifyPackageName);

        }
        super.attr(ns, name, resourceId, type, obj);
    }
}
