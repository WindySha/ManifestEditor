package com.wind.meditor.visitor;

import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.property.AttributeMapper;
import com.wind.meditor.property.ModificationProperty;
import com.wind.meditor.property.PermissionMapper;
import com.wind.meditor.utils.NodeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pxb.android.axml.NodeVisitor;

/**
 * @author Windysha
 */
public class ApplicationTagVisitor extends ModifyAttributeVisitor {

    private ModificationProperty.MetaData curMetaData;
    private List<ModificationProperty.MetaData> metaDataList;
    private List<ModificationProperty.MetaData> deleteMetaDataList;
    private List<ModificationProperty.Provider> providerList;
    private List<ModificationProperty.Activity> activityList;
    private PermissionMapper permissionMapper;
    private AttributeMapper<String> authorityMapper;
    private Map<String, ModificationProperty.Activity> pendingActivitiesByName;
    private List<String> deleteProviderAuthorities;

    private static final String META_DATA_FLAG = "meta_data_flag";

    void setDeleteProviderAuthorities(List<String> deleteProviderAuthorities) {
        this.deleteProviderAuthorities = deleteProviderAuthorities;
    }

    ApplicationTagVisitor(NodeVisitor nv, List<AttributeItem> modifyAttributeList,
                          List<ModificationProperty.MetaData> metaDataList,
                          List<ModificationProperty.MetaData> deleteMetaDataList,
                          PermissionMapper permissionMapper,
                          AttributeMapper<String> authorityMapper,
                          List<ModificationProperty.Provider> providerList,
                          List<ModificationProperty.Activity> activityList) {
        super(nv, modifyAttributeList);
        this.metaDataList = metaDataList;
        this.deleteMetaDataList = deleteMetaDataList;
        this.permissionMapper = permissionMapper;
        this.authorityMapper = authorityMapper;
        this.providerList = providerList;
        this.activityList = activityList;
        this.pendingActivitiesByName = new HashMap<>();
        if (activityList != null) {
            for (ModificationProperty.Activity activity : activityList) {
                pendingActivitiesByName.put(activity.getName(), activity);
            }
        }
    }

    @Override
    public NodeVisitor child(String ns, String name) {
        System.out.println(" ManifestTagVisitor child  --> ns = " + ns + " name = " + name);
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
            if (NodeValue.Application.Provider.TAG_NAME.equals(name)
                    && deleteProviderAuthorities != null && !deleteProviderAuthorities.isEmpty()) {
                return new ProviderDeleteVisitor(this.nv, deleteProviderAuthorities, ns, name);
            }
            NodeVisitor nv = super.child(ns, name);
            if (NodeValue.Application.Activity.TAG_NAME.equals(name)) {
                return new ActivityTagVisitor(nv, pendingActivitiesByName);
            }
            return new ApplicationComponentTagVisitor(nv, permissionMapper, authorityMapper);
        }
        return super.child(ns, name);
    }

    private void addChild(ModificationProperty.MetaData data) {
        curMetaData = data;
        child(META_DATA_FLAG, NodeValue.MetaData.TAG_NAME);
        curMetaData = null;
    }

     public static String getStackTrace(Throwable t){
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement element : t.getStackTrace()){
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void end() {
        if (metaDataList != null) {
            for (ModificationProperty.MetaData data : metaDataList) {
                addChild(data);
            }
        }
        if (providerList != null) {
            for (ModificationProperty.Provider provider : providerList) {
                NodeVisitor nv = super.child(null, "provider");
                new ProviderVisitor(nv, provider);
            }
        }
        if (!pendingActivitiesByName.isEmpty()) {
            for (ModificationProperty.Activity activity : pendingActivitiesByName.values()) {
                NodeVisitor nv = super.child(null, "activity");
                if (nv != null) {
                    ActivityTagVisitor activityVisitor = new ActivityTagVisitor(nv, activity);
                    activityVisitor.end();
                }
            }
        }
        super.end();
    }
}
