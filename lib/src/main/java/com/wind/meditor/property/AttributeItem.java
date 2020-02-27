package com.wind.meditor.property;

import com.wind.meditor.utils.TypedValue;
import com.wind.meditor.xml.ResourceIdXmlReader;

import static com.wind.meditor.utils.NodeValue.MANIFEST_NAMESPACE;

/**
 * @author Windysha
 */
public class AttributeItem {
    private String namespace = MANIFEST_NAMESPACE;  // if no namespace, set it null.
    private String name;
    private int resourceId = -1;
    private int type = TypedValue.TYPE_NULL;
    private Object value;

    // if only change the attrbute value, resourceId, type is not needed to be setted
    public AttributeItem(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public int getResourceId() {
        if (resourceId > 0) {
            return resourceId;
        }
        if (MANIFEST_NAMESPACE.equals(namespace)) {
            resourceId = ResourceIdXmlReader.parseIdFromXml(getName());
        }
        return resourceId;
    }

    public int getType() {
        if (type == TypedValue.TYPE_NULL) {
            if (value instanceof String) {
                type = TypedValue.TYPE_STRING;
            } else if (value instanceof Boolean) {
                type = TypedValue.TYPE_INT_BOOLEAN;
            }
        }
        return type;
    }

    public Object getValue() {
        return value;
    }

    public AttributeItem setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public AttributeItem setResourceId(int resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public AttributeItem setType(int type) {
        this.type = type;
        return this;
    }
}
