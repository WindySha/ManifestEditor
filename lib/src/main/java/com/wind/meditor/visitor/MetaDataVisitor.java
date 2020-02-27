package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.property.ModificationProperty;

import java.util.ArrayList;
import java.util.List;

import pxb.android.axml.NodeVisitor;

/**
 * @author Windysha
 */
public class MetaDataVisitor extends ModifyAttributeVisitor {

    MetaDataVisitor(NodeVisitor nv, ModificationProperty.MetaData metaData) {
        super(nv, convertToAttr(metaData), true);
    }

    private static List<AttributeItem> convertToAttr(ModificationProperty.MetaData metaData) {
        if (metaData == null) {
            return null;
        }
        ArrayList<AttributeItem> list = new ArrayList<>();
        list.add(new AttributeItem("name", metaData.getName()));
        list.add(new AttributeItem("value", metaData.getValue()));
        return list;
    }
}
