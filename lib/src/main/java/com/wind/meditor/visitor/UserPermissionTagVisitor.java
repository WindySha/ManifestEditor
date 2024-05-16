package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.property.PermissionMapper;
import com.wind.meditor.utils.NodeValue;
import com.wind.meditor.utils.PermissionType;
import com.wind.meditor.utils.Utils;

import pxb.android.axml.NodeVisitor;

class UserPermissionTagVisitor extends NodeVisitor {

    private IUsesPermissionGetter permissionGetter;
    private PermissionMapper permissionMapper;

    UserPermissionTagVisitor(NodeVisitor nv, IUsesPermissionGetter permissionGetter, String permissionTobeAdded, PermissionMapper mapper) {
        super(nv);
        this.permissionGetter = permissionGetter;
        this.permissionMapper = mapper;

        if (!Utils.isNullOrEmpty(permissionTobeAdded)) {
            AttributeItem attributeItem = new AttributeItem(NodeValue.UsesPermission.NAME, permissionTobeAdded);
            super.attr(attributeItem.getNamespace(),
                    attributeItem.getName(),
                    attributeItem.getResourceId(),
                    attributeItem.getType(),
                    attributeItem.getValue());
        }
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        if (obj instanceof String && permissionGetter != null) {
            if (NodeValue.UsesPermission.NAME.equals(name) && permissionMapper != null) {
                obj = permissionMapper.map(PermissionType.USES_PERMISSION, (String) obj);
            }
            permissionGetter.onPermissionGetted((String) obj);
        }
        super.attr(ns, name, resourceId, type, obj);
    }

    public interface IUsesPermissionGetter {
        void onPermissionGetted(String permissionName);
    }
}
