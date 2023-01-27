package com.wind.meditor.property;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改的参数
 *
 * @author windysha
 */
public class ModificationProperty {
    
    private List<String> usesPermissionList = new ArrayList<>();
    private List<MetaData> metaDataList = new ArrayList<>();
    private List<MetaData> deleteMetaDataList = new ArrayList<>();

    private List<AttributeItem> applicationAttributeList = new ArrayList<>();
    private List<AttributeItem> manifestAttributeList = new ArrayList<>();
    private List<AttributeItem> usesSdkAttributeList = new ArrayList<>();

    public List<String> getUsesPermissionList() {
        return usesPermissionList;
    }

    public ModificationProperty addUsesPermission(String permissionName) {
        usesPermissionList.add(permissionName);
        return this;
    }

    public List<AttributeItem> getApplicationAttributeList() {
        return applicationAttributeList;
    }

    public ModificationProperty addApplicationAttribute(AttributeItem item) {
        applicationAttributeList.add(item);
        return this;
    }

    public List<MetaData> getMetaDataList() {
        return metaDataList;
    }

    public ModificationProperty addMetaData(MetaData data) {
        metaDataList.add(data);
        return this;
    }

    public List<AttributeItem> getManifestAttributeList() {
        return manifestAttributeList;
    }

    public ModificationProperty addManifestAttribute(AttributeItem item) {
        manifestAttributeList.add(item);
        return this;
    }

    public List<AttributeItem> getUsesSdkAttributeList() {
        return usesSdkAttributeList;
    }

    public ModificationProperty addUsesSdkAttribute(AttributeItem item) {
        usesSdkAttributeList.add(item);
        return this;
    }

    public List<MetaData> getDeleteMetaDataList() {
        return deleteMetaDataList;
    }

    public ModificationProperty addDeleteMetaData(String name) {
        this.deleteMetaDataList.add(new MetaData(name, ""));
        return this;
    }

    public static class MetaData {
        private String name;
        private String value;

        public MetaData(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
