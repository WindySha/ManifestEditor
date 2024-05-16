package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.property.ModificationProperty;
import com.wind.meditor.property.PermissionMapper;
import com.wind.meditor.utils.NodeValue;

import java.util.List;
import pxb.android.axml.NodeVisitor;

/**
 * @author Windysha
 */
public class ApplicationTagVisitor extends ModifyAttributeVisitor {

    private List<ModificationProperty.MetaData> metaDataList;
    private List<ModificationProperty.MetaData> deleteMetaDataList;
    private ModificationProperty.MetaData curMetaData;
    private PermissionMapper permissionMapper;

    private static final String META_DATA_FLAG = "meta_data_flag";

    ApplicationTagVisitor(NodeVisitor nv, List<AttributeItem> modifyAttributeList,
                          List<ModificationProperty.MetaData> metaDataList,
                          List<ModificationProperty.MetaData> deleteMetaDataList,
                          PermissionMapper permissionMapper) {
        super(nv, modifyAttributeList);
        this.metaDataList = metaDataList;
        this.deleteMetaDataList = deleteMetaDataList;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public NodeVisitor child(String ns, String name) {
        if (META_DATA_FLAG.equals(ns)) {
            NodeVisitor nv = super.child(null, name);
            if (curMetaData != null) {
                return new MetaDataVisitor(nv, new ModificationProperty.MetaData(
                        curMetaData.getName(), curMetaData.getValue()));
            }
        } else if (NodeValue.MetaData.TAG_NAME.equals(name)
                && deleteMetaDataList != null && !deleteMetaDataList.isEmpty()) {
            NodeVisitor nv = super.child(ns, name);
            return new DeleteMetaDataVisitor(nv, deleteMetaDataList);
        } else if (NodeValue.Application.COMPONENT_TAGS.contains(name)) {
            return new ApplicationComponentTagVisitor(super.child(ns, name), permissionMapper);
        }
        return super.child(ns, name);
    }

    private void addChild(ModificationProperty.MetaData data) {
        curMetaData = data;
        child(META_DATA_FLAG, NodeValue.MetaData.TAG_NAME);
        curMetaData = null;
    }

    @Override
    public void end() {
        if (metaDataList != null) {
            for (ModificationProperty.MetaData data : metaDataList) {
                addChild(data);
            }
        }
        super.end();
    }
}
