package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import pxb.android.axml.NodeVisitor;

public class ModifyAttributeVisitor extends NodeVisitor {
    private List<AttributeItem> hasBeenAddedAttributeList = new ArrayList<>();
    private List<AttributeItem> mModifyAttributeList;

    // whether the tag where the attribute attached is newly added
    private boolean isNewAddedTag;

    ModifyAttributeVisitor(NodeVisitor nv, List<AttributeItem> modifyAttributeList, boolean isNewAddedTag) {
        super(nv);
        mModifyAttributeList = modifyAttributeList;
        this.isNewAddedTag = isNewAddedTag;

        if (isNewAddedTag) {
            modifyAttr();
        }
    }

    ModifyAttributeVisitor(NodeVisitor nv, List<AttributeItem> modifyAttributeList) {
        this(nv, modifyAttributeList, false);
    }

    public void addModifyAttributeItem(AttributeItem item) {
        if (mModifyAttributeList == null) {
            mModifyAttributeList = new ArrayList<>();
        }
        mModifyAttributeList.add(item);
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        Object newObj = null;
        if (mModifyAttributeList != null) {
            for (AttributeItem attributeItem : mModifyAttributeList) {
                if (attributeItem == null) {
                    continue;
                }

                if ((Utils.isEqual(ns, attributeItem.getNamespace())
                        && Utils.isEqual(name, attributeItem.getName()))
                        || (resourceId >= 0 && resourceId == attributeItem.getResourceId())) {
                    hasBeenAddedAttributeList.add(attributeItem);
                    newObj = attributeItem.getValue();
                    break;
                }
            }
        }

        if (newObj == null) {
            newObj = obj;
        }

        super.attr(ns, name, resourceId, type, newObj);
    }

    @Override
    public void end() {
        if (!isNewAddedTag) {
            modifyAttr();
        }
        super.end();
    }

    private void modifyAttr() {
        if (mModifyAttributeList != null) {
            for (AttributeItem attributeItem : mModifyAttributeList) {
                if (!hasBeenAddedAttributeList.contains(attributeItem)) {
                    super.attr(attributeItem.getNamespace(),
                            attributeItem.getName(),
                            attributeItem.getResourceId(),
                            attributeItem.getType(),
                            attributeItem.getValue());
                }
            }
        }
    }
}
