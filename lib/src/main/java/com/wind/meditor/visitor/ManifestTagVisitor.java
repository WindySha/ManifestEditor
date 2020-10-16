package com.wind.meditor.visitor;

import com.wind.meditor.property.ModificationProperty;
import com.wind.meditor.utils.Log;
import com.wind.meditor.utils.NodeValue;

import java.util.ArrayList;
import java.util.List;

import pxb.android.axml.NodeVisitor;

public class ManifestTagVisitor extends ModifyAttributeVisitor {

    private ModificationProperty properties;

    private List<String> hasIncludedUsesPermissionList = new ArrayList<>();

    private UserPermissionTagVisitor.IUsesPermissionGetter addedPermissionGetter;

    public ManifestTagVisitor(NodeVisitor nv, ModificationProperty properties) {
        super(nv, properties.getManifestAttributeList());
        this.properties = properties;
    }

    @Override
    public NodeVisitor child(String ns, String name) {
        Log.d(" ManifestTagVisitor child  --> ns = " + ns + " name = " + name);

        if (ns != null && (NodeValue.UsesPermission.TAG_NAME).equals(name)) {
            NodeVisitor child = super.child(null, NodeValue.UsesPermission.TAG_NAME);
            return new UserPermissionTagVisitor(child, null, ns, properties);
        }

        NodeVisitor child = super.child(ns, name);
        if (NodeValue.Application.TAG_NAME.equals(name)) {
            return new ApplicationTagVisitor(child, properties, properties.getApplicationAttributeList(),
                    properties.getMetaDataList(), properties.getDeleteMetaDataList());
        } else if (NodeValue.UsesPermission.TAG_NAME.equals(name)) {
            return new UserPermissionTagVisitor(child, getUsesPermissionGetter(), null, properties);
        } else if(NodeValue.Permission.TAG_NAME.equals(name)){
            return new PermissionTagVisitor(child, properties);
        }
        return child;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        Log.d(" ManifestTagVisitor attr  --> ns = " + ns + " name = " +
                name + " resourceId=" + resourceId + " obj = " + obj);
        if (NodeValue.Manifest.PACKAGE.equals(name) && type == TYPE_STRING) {
            properties.setOriginPackageName((String) obj);
        }
        super.attr(ns, name, resourceId, type, obj);
    }

    @Override
    public void end() {
        List<String> list = properties.getUsesPermissionList();
        if (list != null && list.size() > 0) {
            for (String permissionName : list) {
                // permission is not added.
                if (!hasIncludedUsesPermissionList.contains(permissionName)) {
                    // pass permission name to child by name space
                    child(permissionName, NodeValue.UsesPermission.TAG_NAME);
                }
            }
        }
        super.end();
    }

    private UserPermissionTagVisitor.IUsesPermissionGetter getUsesPermissionGetter() {
        if (addedPermissionGetter == null) {
            addedPermissionGetter = permissionName -> {
                if (!hasIncludedUsesPermissionList.contains(permissionName)) {
                    hasIncludedUsesPermissionList.add(permissionName);
                }
            };
        }
        return addedPermissionGetter;
    }
}
