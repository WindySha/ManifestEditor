package com.wind.meditor.visitor;

import com.wind.meditor.property.ModificationProperty;
import com.wind.meditor.utils.Log;
import com.wind.meditor.utils.NodeValue;

import java.util.ArrayList;
import java.util.List;

import pxb.android.axml.NodeVisitor;

public class ManifestTagVisitor extends ModifyAttributeVisitor {

    private ModificationProperty modificationProperty;

    private List<String> hasIncludedUsesPermissionList = new ArrayList<>();

    private UserPermissionTagVisitor.IUsesPermissionGetter addedPermissionGetter;

    public ManifestTagVisitor(NodeVisitor nv, ModificationProperty properties) {
        super(nv, properties.getManifestAttributeList());
        this.modificationProperty = properties;
    }

    @Override
    public NodeVisitor child(String ns, String name) {

        if (ns != null && (NodeValue.UsesPermission.TAG_NAME).equals(name)) {
            NodeVisitor child = super.child(null, NodeValue.UsesPermission.TAG_NAME);
            return new UserPermissionTagVisitor(child, null, ns, modificationProperty.getPermissionMapper());
        }

        NodeVisitor child = super.child(ns, name);
        if (NodeValue.Application.TAG_NAME.equals(name)) {
            return new ApplicationTagVisitor(child, modificationProperty.getApplicationAttributeList(),
                    modificationProperty.getMetaDataList(), modificationProperty.getDeleteMetaDataList(),
                    modificationProperty.getPermissionMapper(), modificationProperty.getAuthorityMapper(),
                    modificationProperty.getProviderList(), modificationProperty.getActivityList());
        }

        if (NodeValue.UsesSDK.TAG_NAME.equals(name)) {
            return new ModifyAttributeVisitor(child, modificationProperty.getUsesSdkAttributeList());
        }

        if (NodeValue.UsesPermission.TAG_NAME.equals(name)) {
            return new UserPermissionTagVisitor(child, getUsesPermissionGetter(), null, modificationProperty.getPermissionMapper());
        }

        if (NodeValue.Permission.TAG_NAME.equals(name)) {
            return new PermissionTagVisitor(child, modificationProperty.getPermissionMapper());
        }
        return child;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        Log.d(" ManifestTagVisitor attr  --> ns = " + ns + " name = " +
                name + " resourceId=" + resourceId + " obj = " + obj);
        super.attr(ns, name, resourceId, type, obj);
    }

    @Override
    public void end() {
        List<String> list = modificationProperty.getUsesPermissionList();
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
