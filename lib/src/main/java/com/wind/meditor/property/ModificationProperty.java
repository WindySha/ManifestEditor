package com.wind.meditor.property;

import com.wind.meditor.utils.NodeValue;

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

    public List<String> getUsesPermissionList() {
        return usesPermissionList;
    }
    private String originPackageName;
    public ModificationProperty addUsesPermission(String permissionName) {
        usesPermissionList.add(permissionName);
        return this;
    }
    //打包后的包名
    public String getModifyPackageName(){
        for(int i = 0; i< manifestAttributeList.size(); i++){
            AttributeItem attributeItem = manifestAttributeList.get(i);
            if(NodeValue.Manifest.PACKAGE.equals(attributeItem.getName())){
                return (String) attributeItem.getValue();
            }
        }
        return originPackageName;
    }

    public String getOriginPackageName() {
        return originPackageName;
    }

    public void setOriginPackageName(String packageName) {
        this.originPackageName = packageName;
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
