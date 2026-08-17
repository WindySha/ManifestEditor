package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.property.ModificationProperty;

import java.util.ArrayList;
import java.util.List;

import pxb.android.axml.NodeVisitor;

public class ProviderVisitor extends ModifyAttributeVisitor{
    ProviderVisitor(NodeVisitor nv, ModificationProperty.Provider provider) {
        super(nv, provider == null ? null : provider.getAttributes(), true);

        String filterAction = provider == null ? null : provider.getFilterAction();
        if (filterAction != null) {
            NodeVisitor intentFilter = super.child(null, "intent-filter");
            NodeVisitor action = intentFilter.child(null, "action");

            List<AttributeItem> list = new ArrayList<>();
            list.add(new AttributeItem("name", filterAction));
            new ModifyAttributeVisitor(action, list, true);
        }
    }
}
