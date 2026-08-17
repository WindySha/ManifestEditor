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
    private List<Activity> activityList = new ArrayList<>();

    private PermissionMapper permissionMapper;
    private AttributeMapper<String> providerAuthorityMapper;
    private List<Provider> providerList = new ArrayList<>();
    private List<String> deleteProviderAuthorities = new ArrayList<>();

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

    public ModificationProperty addProvider(List<AttributeItem> attributes, String filterAction){
        providerList.add(new Provider(attributes, filterAction));
        return this;
    }

    public ModificationProperty addActivity(Activity activity) {
        activityList.add(activity);
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

    public List<Provider> getProviderList(){
        return providerList;
    }

    public List<String> getDeleteProviderAuthorities() {
        return deleteProviderAuthorities;
    }

    public ModificationProperty addDeleteProviderAuthorities(String authorities) {
        deleteProviderAuthorities.add(authorities);
        return this;
    }

    public List<Activity> getActivityList() {
        return activityList;
    }

    public ModificationProperty addDeleteMetaData(String name) {
        this.deleteMetaDataList.add(new MetaData(name, ""));
        return this;
    }

    public PermissionMapper getPermissionMapper() {
        return permissionMapper;
    }

    public ModificationProperty setPermissionMapper(PermissionMapper mapper) {
        this.permissionMapper = mapper;
        return this;
    }

    public AttributeMapper<String> getAuthorityMapper() {
        return providerAuthorityMapper;
    }

    public ModificationProperty setAuthorityMapper(AttributeMapper<String> mapper) {
        this.providerAuthorityMapper = mapper;
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

    /**
     * A {@code <provider>} to add, carried as typed {@link AttributeItem}s rather than string pairs.
     *
     * The type matters: {@code exported} and {@code grantUriPermissions} are read by the platform
     * with {@code TypedArray.getBoolean}, which returns the default for any non-integer value -- so a
     * boolean written as the string {@code "true"} is silently seen as false. Passing an
     * {@link AttributeItem} whose value is a {@link Boolean} lets the visitor emit it as
     * {@code TYPE_INT_BOOLEAN}, which the platform actually honours.
     */
    public static class Provider{
        private final List<AttributeItem> attributes;
        private final String filterAction;
        public Provider(List<AttributeItem> attributes, String filterAction){
            this.attributes = attributes;
            this.filterAction = filterAction;
        }
        public List<AttributeItem> getAttributes(){
            return attributes;
        }
        public String getFilterAction(){
            return filterAction;
        }
    }

    public static class Activity {
        private String name;
        private Boolean exported;

        public Activity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getExported() {
            return exported;
        }

        public void setExported(Boolean exported) {
            this.exported = exported;
        }
    }
}
