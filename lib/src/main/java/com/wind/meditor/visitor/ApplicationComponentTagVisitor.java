package com.wind.meditor.visitor;

import com.wind.meditor.property.PermissionMapper;
import com.wind.meditor.utils.NodeValue;
import com.wind.meditor.utils.PermissionType;
import pxb.android.axml.NodeVisitor;

public class ApplicationComponentTagVisitor extends NodeVisitor {
    private final PermissionMapper permissionMapper;

    ApplicationComponentTagVisitor(NodeVisitor nv, PermissionMapper permissionMapper) {
        super(nv);
        this.permissionMapper = permissionMapper;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        if (isPermissionTag(name) && obj instanceof String && permissionMapper != null) {
            obj = permissionMapper.map(PermissionType.COMPONENT_PERMISSION, (String) obj);
        }
        super.attr(ns, name, resourceId, type, obj);
    }

    private boolean isPermissionTag(String name) {
        return name.equals(NodeValue.Application.Component.PERMISSION) ||
               name.equals(NodeValue.Application.Provider.READ_PERMISSION) ||
               name.equals(NodeValue.Application.Provider.WRITE_PERMISSION);
    }
}
