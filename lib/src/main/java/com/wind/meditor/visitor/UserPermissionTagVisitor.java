package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.utils.NodeValue;
import com.wind.meditor.utils.Utils;

import pxb.android.axml.NodeVisitor;

class UserPermissionTagVisitor extends NodeVisitor {

    private IUsesPermissionGetter permissionGetter;

    UserPermissionTagVisitor(NodeVisitor nv, IUsesPermissionGetter permissionGetter, String permissionTobeAdded) {
        super(nv);
        this.permissionGetter = permissionGetter;

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
            permissionGetter.onPermissionGetted((String) obj);
        }
        super.attr(ns, name, resourceId, type, obj);
    }

    public interface IUsesPermissionGetter {
        void onPermissionGetted(String permissionName);
    }
}
