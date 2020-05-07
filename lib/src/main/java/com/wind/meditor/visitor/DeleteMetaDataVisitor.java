package com.wind.meditor.visitor;

import com.wind.meditor.property.ModificationProperty;

import java.util.List;

import pxb.android.axml.NodeVisitor;

/**
 * @author Windysha
 */
public class DeleteMetaDataVisitor extends NodeVisitor {

    private List<ModificationProperty.MetaData> deleteMetaDataList;
    private boolean shouldDeleteNode = false;  // 此metaData的value包含在deleteMetaDataList中，则删除metaData内容

    DeleteMetaDataVisitor(NodeVisitor nv, List<ModificationProperty.MetaData> deleteMetaDataList) {
        super(nv);
        this.deleteMetaDataList = deleteMetaDataList;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        if ("name".equals(name) && !shouldDeleteNode) {
            for (ModificationProperty.MetaData data : deleteMetaDataList) {
                if (data.getName() != null && data.getName().equals(obj)) {
                    shouldDeleteNode = true;
                    break;
                }
            }
        }
        if (!shouldDeleteNode) {
            super.attr(ns, name, resourceId, type, obj);
        }
    }
}
