package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.property.ModificationProperty;

import java.util.ArrayList;
import java.util.List;

import pxb.android.axml.NodeVisitor;

public class ProviderVisitor extends ModifyAttributeVisitor{
    ProviderVisitor(NodeVisitor nv, ModificationProperty.Provider provider) {
        super(nv, convertToAttr(provider), true);

        NodeVisitor intentFilter = super.child(null, "intent-filter");
        NodeVisitor action = intentFilter.child(null, "action");
        action.attr(null, "name", -1, NodeVisitor.TYPE_STRING, provider.getFilterNameValue());
    }

    private static List<AttributeItem> convertToAttr(ModificationProperty.Provider provider) {
        if (provider == null) {
            return null;
        }
        ArrayList<AttributeItem> list = new ArrayList<>();
        for (String keys : provider.getNameValue().keySet()){
            list.add(new AttributeItem(keys, provider.getNameValue().get(keys)));
        }
        return list;
    }
}
