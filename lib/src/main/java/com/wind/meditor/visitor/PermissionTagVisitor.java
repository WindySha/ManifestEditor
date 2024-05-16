package com.wind.meditor.visitor;

import com.wind.meditor.property.PermissionMapper;
import com.wind.meditor.utils.NodeValue;
import com.wind.meditor.utils.PermissionType;
import pxb.android.axml.NodeVisitor;

public class PermissionTagVisitor extends NodeVisitor {
    private final PermissionMapper permissionMapper;

    PermissionTagVisitor(NodeVisitor nv, PermissionMapper permissionMapper) {
        super(nv);
        this.permissionMapper = permissionMapper;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        if (NodeValue.Permission.NAME.equals(name) && obj instanceof String && permissionMapper != null) {
            obj = permissionMapper.map(PermissionType.DECLARED_PERMISSION, (String) obj);
        }
        super.attr(ns, name, resourceId, type, obj);
    }
}
