package com.wind.meditor;

import com.wind.meditor.base.BaseCommand;
import com.wind.meditor.core.ApkSigner;
import com.wind.meditor.core.FileProcesser;
import com.wind.meditor.property.AttributeItem;
import com.wind.meditor.property.ModificationProperty;
import com.wind.meditor.utils.FileTypeUtils;
import com.wind.meditor.utils.Log;
import com.wind.meditor.utils.NodeValue;
import com.wind.meditor.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Windysha
 */
public class ManifestEditorMain extends BaseCommand {

    private static final String MULTI_NAME_SEPERATER = ":";
    private static final String ANDROID_NAMESPACE = "android-";

    @Opt(opt = "o", longOpt = "output", description = "output modified xml or apk file, default is " +
            "$source_apk_dir/[file-name]-new.xml or [file-name]-new-unsigned.apk", argName = "output-file")
    private String output;  // the output file path

    @Opt(opt = "f", longOpt = "force", hasArg = false, description = "force overwrite")
    private boolean forceOverwrite = false;

    @Opt(opt = "s", longOpt = "signApk", hasArg = false, description = "use jarsigner to sign the output apk file")
    private boolean needSignApk = false;

    @Opt(opt = "pkg", longOpt = "packageName", description = "set the android manifest package name",
            argName = "new-package-name")
    private String packageName;

    @Opt(opt = "vc", longOpt = "versionCode", description = "set the app version code",
            argName = "new-version-code")
    private int versionCode;

    @Opt(opt = "vn", longOpt = "versionName", description = "set the app version name",
            argName = "new-version-name")
    private String versionName;

    @Opt(opt = "d", longOpt = "debuggable", description = "set 1 to make the app debuggable = true, " +
            "set 0 to make the app debuggable = false", argName = "0 or 1")
    private int debuggable = -1;

    @Opt(opt = "an", longOpt = "applicationName", description = "set the app entry application name",
            argName = "new-application-name")
    private String applicationName;

    @Opt(opt = "up", longOpt = "usesPermission", description = "add the app uses permission " +
            "name to the manifest file, multi option is supported", argName = "uses-permission-name")
    private List<String> usesPermissionList = new ArrayList<>();

    @Opt(opt = "ma", longOpt = "manifestAttribute", description = "set the app manifest attribute, " +
            " name and value should be separated by " + MULTI_NAME_SEPERATER +
            " , if name is in android namespace, prefix \"" + ANDROID_NAMESPACE + "\" should be set" +
            ", multi option is supported", argName = "manifest-attribute-name-value")
    private List<String> manifestAttributeList = new ArrayList<>();

    @Opt(opt = "aa", longOpt = "applicationAttribute", description = "set the application attribute, " +
            " name and value should be separated by " + MULTI_NAME_SEPERATER +
            " , if name is in android namespace, prefix \"" + ANDROID_NAMESPACE + "\" should be set" +
            ", multi option is supported", argName = "application-attribute-name-value")
    private List<String> applicationAttributeList = new ArrayList<>();

    @Opt(opt = "md", longOpt = "metaData", description = "add the meta data, " +
            " name and value should be separated by " + MULTI_NAME_SEPERATER +
            ", multi option is supported", argName = "meta-data-name-value")
    private List<String> metaDataList = new ArrayList<>();

    @Opt(opt = "dmd", longOpt = "deleteMetaDataList", description = "delete the meta data name" +
            ", multi option is supported", argName = "delete-meta-data-name")
    private List<String> deleteMetaDataList = new ArrayList<>();

    public static void main(String... args) {
        new ManifestEditorMain().doMain(args);
    }

    @Override
    protected void doCommandLine() throws Exception {
        if (remainingArgs.length != 1) {
            if (remainingArgs.length == 0) {
                Log.e("Please choose one xml or apk file you want to process. ");
            }
            if (remainingArgs.length > 1) {
                Log.e("This tool can only used with one xml or apk file.");
            }
            usage();
            return;
        }

        String srcFilePath = remainingArgs[0];
        File srcFile = new File(srcFilePath);

        if (!srcFile.exists()) {
            Log.e(String.format("input file %s  do not exist, please check it!", srcFilePath));
            usage();
            return;
        }

        boolean isMainfestFile = FileTypeUtils.isAndroidManifestFile(srcFilePath);
        boolean isApkFile = false;
        if (!isMainfestFile) {
            isApkFile = FileTypeUtils.isApkFile(srcFilePath);
        }

        if (!isMainfestFile && !isApkFile) {
            Log.e("input file should be manifest file or apk file !!!");
            usage();
            return;
        }

        String signedApkPath = "";

        if (output == null || output.length() == 0) {
            if (isMainfestFile) {
                output = getBaseName(srcFilePath) + "-new.xml";
            }
            if (isApkFile) {
                output = getBaseName(srcFilePath) + "-unsigned.apk";
                if (needSignApk) {
                    signedApkPath = getBaseName(srcFilePath) + "-signed.apk";
                }
            }
        } else {
            if (isApkFile && needSignApk) {
                signedApkPath = getBaseName(output) + "-signed.apk";
            }
        }

        File outputFile = new File(output);
        if (outputFile.exists() && !forceOverwrite) {
            Log.e(output + " exists, use --force to overwrite the output file");
            usage();
            return;
        }

        Log.i("output file path --> " + output);

        ModificationProperty modificationProperty = composeProperty();

        if (isMainfestFile) {
            Log.i("Start to process manifest file ");
            FileProcesser.processManifestFile(srcFilePath, output, modificationProperty);
        } else if (isApkFile) {
            Log.i("Start to process apk.");
            FileProcesser.processApkFile(srcFilePath, output, modificationProperty);

            if (needSignApk) {
                Log.i("Start to sign the apk.");

                String parentPath = null;
                String keyStoreFilePath = null;
                File parentFile = new File(output).getParentFile();

                if (parentFile != null) {
                    parentPath = parentFile.getAbsolutePath();
                    keyStoreFilePath = parentPath + File.separator + "keystore";
                } else {
                    // 当前命令行所在的目录
                    keyStoreFilePath = "keystore";
                }
                Log.d(" parentPath = " + parentPath + " keyStoreFilePath = " + keyStoreFilePath);

                Log.i(" output unsigned apk path = " + output);
                Log.i(" output signed apk path = " + signedApkPath);


                // cannot use File.separator to seperate assets/new_keystore，or IOException is thrown on window os
                Utils.copyFileFromJar("assets/new_keystore", keyStoreFilePath);

                ApkSigner.signApk(output, keyStoreFilePath, signedApkPath);

                // delete the keystore file finally
                File keyStoreFile = new File(keyStoreFilePath);
                if (keyStoreFile.exists()) {
                    keyStoreFile.delete();
                }
            }
        }
    }

    private ModificationProperty composeProperty() {
        ModificationProperty property = new ModificationProperty();

        if (!Utils.isNullOrEmpty(packageName)) {
            property.addManifestAttribute(new AttributeItem(NodeValue.Manifest.PACKAGE, packageName).setNamespace(null));
        }

        if (versionCode > 0) {
            property.addManifestAttribute(new AttributeItem(NodeValue.Manifest.VERSION_CODE, versionCode));
        }

        if (!Utils.isNullOrEmpty(versionName)) {
            property.addManifestAttribute(new AttributeItem(NodeValue.Manifest.VERSION_NAME, versionName));
        }

        if (debuggable >= 0) {
            property.addApplicationAttribute(new AttributeItem(NodeValue.Application.DEBUGGABLE, debuggable != 0));
        }

        if (!Utils.isNullOrEmpty(applicationName)) {
            property.addApplicationAttribute(new AttributeItem(NodeValue.Application.NAME, applicationName));
        }

        for (String permission : usesPermissionList) {
            property.addUsesPermission(permission);
        }

        for (String manfestAttr : manifestAttributeList) {
            String[] nameValue = manfestAttr.split(MULTI_NAME_SEPERATER);
            if (nameValue.length == 2) {
                if (nameValue[0].trim().startsWith(ANDROID_NAMESPACE)) {
                    property.addManifestAttribute(new AttributeItem(
                            nameValue[0].trim().substring(ANDROID_NAMESPACE.length()), nameValue[1].trim()));
                } else {
                    property.addManifestAttribute(new AttributeItem(nameValue[0].trim(), nameValue[1].trim()).setNamespace(null));
                }
            }
        }

        for (String applicationAttr : applicationAttributeList) {
            String[] nameValue = applicationAttr.split(MULTI_NAME_SEPERATER);
            if (nameValue.length == 2) {
                if (nameValue[0].trim().startsWith(ANDROID_NAMESPACE)) {
                    property.addApplicationAttribute(new AttributeItem(
                            nameValue[0].trim().substring(ANDROID_NAMESPACE.length()), nameValue[1].trim()));
                } else {
                    property.addApplicationAttribute(new AttributeItem(nameValue[0].trim(), nameValue[1].trim()).setNamespace(null));
                }
            }
        }

        for (String metaData : metaDataList) {
            String[] nameValue = metaData.split(MULTI_NAME_SEPERATER);

            if (nameValue.length == 2) {
                property.addMetaData(new ModificationProperty.MetaData(nameValue[0], nameValue[1]));
            }
        }

        for (String metaData : deleteMetaDataList) {
            property.addDeleteMetaData(metaData);
        }

//        property.addManifestAttribute(new AttributeItem(NodeValue.Manifest.PACKAGE, "wind.new.pkg.name111").setNamespace(null))
//                .addManifestAttribute(new AttributeItem(NodeValue.Manifest.VERSION_CODE, 1))
//                .addManifestAttribute(new AttributeItem(NodeValue.Manifest.VERSION_NAME, "1123"))
//                .addUsesPermission("android.permission.READ_EXTERNAL_STORAGE")
//                .addUsesPermission("android.permission.WRITE_EXTERNAL_STORAGE")
//                .addMetaData(new ModificationProperty.MetaData("aa", "11"))
//                .addMetaData(new ModificationProperty.MetaData("aa", "22"))
//                .addApplicationAttribute(new AttributeItem(NodeValue.Application.DEBUGGABLE, false))
//                .addApplicationAttribute(new AttributeItem(NodeValue.Application.NAME, "my.app.name.MyTestApplication"))
//                .addApplicationAttribute(new AttributeItem("appComponentFactory", "my.app.name.MyTestApplication111"));

        return property;
    }
}
