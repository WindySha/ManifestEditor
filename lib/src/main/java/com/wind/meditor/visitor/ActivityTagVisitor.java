package com.wind.meditor.visitor;

import com.wind.meditor.property.ModificationProperty;

import java.util.Map;

import pxb.android.axml.NodeVisitor;

/**
 * Handles Activity nodes in AndroidManifest.xml.
 */
public class ActivityTagVisitor extends NodeVisitor {

    private ModificationProperty.Activity targetActivity;
    private boolean activityWritten = false;
    private boolean replaceExistingActivity = false;
    private String currentActivityName = null;
    private boolean replacementApplied = false;
    private Map<String, ModificationProperty.Activity> replacementActivities;
    private boolean nameResolved = false;

    // Creates a visitor for adding a new Activity.
    public ActivityTagVisitor(NodeVisitor nv, ModificationProperty.Activity activity) {
        super(nv);
        this.targetActivity = activity;
        this.replaceExistingActivity = false;
    }

    // Creates a visitor for replacing an existing Activity.
    public ActivityTagVisitor(NodeVisitor nv, Map<String, ModificationProperty.Activity> activityReplacementMap) {
        super(nv);
        this.replacementActivities = activityReplacementMap;
        this.replaceExistingActivity = true;
    }

    @Override
    public void attr(String ns, String name, int resourceId, int type, Object obj) {
        if (replaceExistingActivity) {
            handleReplacementAttribute(ns, name, resourceId, type, obj);
        } else {
            super.attr(ns, name, resourceId, type, obj);
        }
    }

    private void handleReplacementAttribute(String ns, String name, int resourceId, int type, Object obj) {
        if (!nameResolved && "name".equals(name) && obj instanceof String) {
            currentActivityName = (String) obj;
            ModificationProperty.Activity replacementActivity = replacementActivities.get(currentActivityName);
            if (replacementActivity != null) {
                targetActivity = replacementActivity;
                replacementActivities.remove(currentActivityName);
                nameResolved = true;
            }
        }

        if (shouldReplaceAttribute(name)) {
            Object newValue = getNewAttributeValue(name);
            if (newValue != null) {
                super.attr(ns, name, resourceId, type, newValue);
                replacementApplied = true;
                return;
            }
        }

        super.attr(ns, name, resourceId, type, obj);
    }

    @Override
    public void end() {
        if (replaceExistingActivity) {
            if (!replacementApplied && isTargetActivity()) {
                addMissingAttributes();
            }
        } else if (!activityWritten) {
            setActivityAttributes();
        }
        super.end();
    }

    /**
     * Returns true when the given attribute should be replaced.
     */
    private boolean shouldReplaceAttribute(String attributeName) {
        if (!isTargetActivity()) {
            return false;
        }

        if ("exported".equals(attributeName)) {
            return targetActivity != null && targetActivity.getExported() != null;
        }

        return false;
    }

    /**
     * Returns the replacement value for the given attribute.
     */
    private Object getNewAttributeValue(String attributeName) {
        if (targetActivity == null) {
            return null;
        }

        if ("exported".equals(attributeName)) {
            return targetActivity.getExported() ? 1 : 0;
        }

        return null;
    }

    /**
     * Returns true when the current node matches the target Activity.
     */
    private boolean isTargetActivity() {
        return currentActivityName != null &&
               targetActivity != null &&
               currentActivityName.equals(targetActivity.getName());
    }

    /**
     * Adds attributes that exist on the new Activity but are missing on the current one.
     */
    private void addMissingAttributes() {
        if (targetActivity == null) {
            return;
        }

        if (targetActivity.getExported() != null) {
            super.attr("http://schemas.android.com/apk/res/android", "exported",
                0x01010010,
                18, targetActivity.getExported() ? 1 : 0);
        }
    }

    /**
     * Writes the base Activity attributes for a newly added node.
     */
    private void setActivityAttributes() {
        if (targetActivity == null || targetActivity.getName() == null) {
            return;
        }

        super.attr("http://schemas.android.com/apk/res/android", "name",
            0x01010003,
            3, targetActivity.getName());

        if (targetActivity.getExported() != null) {
            super.attr("http://schemas.android.com/apk/res/android", "exported",
                0x01010010,
                18, targetActivity.getExported() ? 1 : 0);
        }
        activityWritten = true;
    }
}
