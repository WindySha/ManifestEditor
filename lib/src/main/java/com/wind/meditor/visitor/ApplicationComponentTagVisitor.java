package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeMapper;
import com.wind.meditor.property.PermissionMapper;
import com.wind.meditor.utils.NodeValue;
import com.wind.meditor.utils.PermissionType;
import pxb.android.axml.NodeVisitor;

import java.util.Arrays;

import static java.util.stream.Collectors.joining;

public class ApplicationComponentTagVisitor extends NodeVisitor {
    private final PermissionMapper permissionMapper;
    private final AttributeMapper<String> authorityMapper;

    ApplicationComponentTagVisitor(NodeVisitor nv,
                                   PermissionMapper permissionMapper,
                                   AttributeMapper<String> authorityMapper) {
        super(nv);
        this.permissionMapper = permissionMapper;
        this.authorityMapper = authorityMapper;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        if (obj instanceof String) {
            if (isPermissionTag(name) && permissionMapper != null) {
                obj = permissionMapper.map(PermissionType.COMPONENT_PERMISSION, (String) obj);
            }
            if (NodeValue.Application.Provider.AUTHORITIES.equals(name) && authorityMapper != null) {
                obj = mapAuthorities((String) obj);
            }
        }
        super.attr(ns, name, resourceId, type, obj);
    }

    private Object mapAuthorities(String authorities) {
        return !authorities.contains(";") ? authorityMapper.map(authorities) :
                Arrays.stream(authorities.split(";")).map(s -> authorityMapper.map(s.trim())).collect(joining("; "));
    }

    private boolean isPermissionTag(String name) {
        return name.equals(NodeValue.Application.Component.PERMISSION) ||
               name.equals(NodeValue.Application.Provider.READ_PERMISSION) ||
               name.equals(NodeValue.Application.Provider.WRITE_PERMISSION);
    }
}
