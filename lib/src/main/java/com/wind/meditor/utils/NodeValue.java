package com.wind.meditor.utils;

public final class NodeValue {
    private NodeValue(){}

    public static final String NAME="name";
    public static final String VALUE="value";
    public static final String LABEL="label";

    public static final String MANIFEST_NAMESPACE = "http://schemas.android.com/apk/res/android";

    public static final class Manifest{
        public static final String TAG_NAME = "manifest";

        public static final String VERSION_CODE="versionCode";
        public static final String VERSION_NAME="versionName";
        public static final String INSTALL_LOCATION="installLocation";
        public static final String SHARDE_USER_ID="sharedUserId";
        public static final String SHARED_USER_LABEL="sharedUserLabel";
        public static final String PACKAGE="package";
    }

    public static final class UsesSDK{
        public static final String TAG_NAME = "uses-sdk";

        public static final String MAX_SDK_VERSION="maxSdkVersion";
        public static final String MIN_SDK_VERSION="minSdkVersion";
        public static final String TARGET_SDK_VERSION="targetSdkVersion";
    }

    public static final class UsesPermission{
        public static final String TAG_NAME = "uses-permission";

        public static final String NAME="name";
        public static final String MAX_SDK_VERSION="maxSdkVersion";
    }

    public static final class MetaData{
        public static final String TAG_NAME = "meta-data";

        public static final String NAME="name";
        public static final String VALUE="value";
        public static final String RESOURCE="resource";
    }


    public static final class Application{

        public static final String TAG_NAME = "application";

        public static final String NAME="name";
        public static final String VALUE="value";
        public static final String LABEL="label";
        public static final String THEME="theme";
        public static final String ICON="icon";
        public static final String PERSISTENT="persistent";
        public static final String ALLOWBACKUP="allowBackup";
        public static final String LARGEHEAP="largeHeap";
        public static final String DEBUGGABLE="debuggable";
        public static final String EXTRACTNATIVELIBS="extractNativeLibs";
        public static final String HARDWAREACCELERATED="hardwareAccelerated";
        public static final String FULLBACKUPONLY="fullBackupOnly";
        public static final String VMSAFEMODE="vmSafeMode";
        public static final String ENABLED="enabled";
        public static final String DESCRIPTION="description";
        public static final String PROCESS="process";
    }


}
