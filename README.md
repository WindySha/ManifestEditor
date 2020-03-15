#  **ManifestEditor**
This is a tool used to modify Android Manifest binary file.  
此工具用于修改AndroidManifest二进制文件。比如，更改Manifest文件中的app包名，版本号，更改或新增app入口Application的类名，更改或新增debuggable的属性，增加usesPermission标签，增加meta-data标签等。
同时，为了更方便使用，提供了直接修改Apk包中的Manifest文件，并对修改后的Apk进行签名的功能。

比较常见的修改AndroidManifest二进制文件的工具，大致有这些：
1. [apkeditor](https://github.com/8enet/apkeditor)
2. [AXMLEditor](https://github.com/fourbrother/AXMLEditor)

但是，这些工具都有一个致命的问题: 新增属性无法被Android系统解析。  
比如，在application标签下增加debuggable=true属性，安装后的App并不是debuggable的。

本工具并不存在此问题。当然，可能会存在其他一些问题，并未作充分测试。

此项目基于[axml](https://github.com/Sable/axml)，并在其基础做了二次封装和一些优化，使用起来更加方便。

This tool is used in project [Xpatch](https://github.com/WindySha/Xpatch)
#  **Jar包下载**
打开release页面下载，或[点击下载V1.0.1](https://github.com/WindySha/ManifestEditor/releases/download/v1.0.1/ManifestEditor-1.0.1.jar)

# **查看帮助文档**
```
$ java -jar ../ManifestEditor.jar -h
```
得到使用文档：
```
options:
 -aa,--applicationAttribute <application-attribute-name-value>
                             set the application attribute,  name and value shou
                             ld be separated by : , if name is in android namesp
                             ace, prefix "android-" should be set, multi option 
                             is supported
 -an,--applicationName <new-application-name>
                             set the app entry application name
 -d,--debuggable <0 or 1>    set 1 to make the app debuggable = true, set 0 to m
                             ake the app debuggable = false
 -f,--force                  force overwrite
 -h,--help                   Print this help message
 -ma,--manifestAttribute <manifest-attribute-name-value>
                             set the app manifest attribute,  name and value sho
                             uld be separated by : , if name is in android names
                             pace, prefix "android-" should be set, multi option
                              is supported
 -o,--output <output-file>   output modified xml or apk file, default is $source
                             _apk_dir/[file-name]-new.xml or [file-name]-new-uns
                             igned.apk
 -pkg,--packageName <new-package-name>set the android manifest package name
 -s,--signApk                use jarsigner to sign the output apk file
 -up,--usesPermission <uses-permission-name>
                             add the app uses permission name to the manifest fi
                             le, multi option is supported
 -vc,--versionCode <new-version-code>set the app version code
 -vn,--versionName <new-version-name>set the app version name
version: 1.0.0
```
# **修改Manifest文件**
### 1. 修改Manifest中app包名: `-pkg`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -pkg com.test.newpackage
```
在AndroidManifest.xml文件相同的目录下，会生成一个新的xml文件：AndroidManifest-new.xml

这个新的manifest文件中，package被改成了`com.test.newpackage`。
### 2. 新增debuggable = true的属性: `-d`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -d 1
```
新的manifest文件中，Application标签下，增加了`android:debuggable = "true"`属性。
如果需要将debuggable改为false，只需：
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -d 0
```
### 3. 修改Manifest文件中的versionCode和versionName: `-vc` `-vn`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -vc 100 -vn 1.0.0
```
新的manifest文件中，versionCode被改成了`100`，versionName被改成了`1.0.0`。
### 4. 修改Manifest文件中的applicationName: `-an`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -an com.test.new.MyApplication
```
新的manifest文件中，application标签下的name被改为：`android:name="com.test.new.MyApplication"`。
### 5. 新增Manifest文件中的usesPermission标签:`-up`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -up android.permission.READ_EXTERNAL_STORAGE -up android.permission.WRITE_EXTERNAL_STORAGE
```
新的manifest文件中，新增了读写sdcard两个权限标签。假如原Manifest文件中已经存在相关权限标签，则不会增加新的。
### 6. 增加或修改顶层的manifest标签下的其他属性:`-ma`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -ma android-compileSdkVersion:28 -ma android-compileSdkVersionCodename:9
```
新的manifest文件中，顶层的manifest标签下新增或者修改的标签为：
```
<manifest
   ...
    android:compileSdkVersion="28"
    android:compileSdkVersionCodename="9">
```
对于非android命名空间下的属性，去掉命令中的`android-`即可，暂不支持其他命名空间下的属性的更改。比如：
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -ma platformBuildVersionCode:100
```
改动的属性是platformBuildVersionCode：
```
<manifest
   ...
   platformBuildVersionCode="100">
```
### 7. 增加或修改application标签下的其他属性: `-aa`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -aa android-allowBackup:false
```
新的manifest文件中，application标签下新增或者修改的标签为：
```
<application
   ...
   android:allowBackup="false"
    ...
>
```
对于非android命名空间下的属性，去掉命令中的`android-`即可，暂不支持其他命名空间下的属性的更改。
### 8. 新Manifest文件输出到指定目录: `-o`
```
$ java -jar ../ManifestEditor.jar ../AndroidManifest.xml -o ../new_androidmanifest.xml -d 1
```
将debuggable改为true后的Manifest文件输出为`new_androidmanifest.xml`
# **修改Apk中的Manifest文件**
```
$ java -jar ../ManifestEditor.jar ../original.apk -o ../new_build_unsigned.apk -d 1
```
将original.apk文件里的manifest的debuggable属性改为true后，输出未签名的新apk: `new_build_unsigned.apk`。
如果需要用内置的签名文件对apk进行签名，加上`-s`即可：
```
$ java -jar ../ManifestEditor.jar ../original.apk -o ../new_build.apk -d 1 -s
```
`new_build.apk`文件目录会生成另外一个签名后的apk：`new_build_signed.apk`。

默认使用的是jarsigner命令对apk签名，假如签名失败，可自行对`new_build.apk`进行签名。
# **Android或者Java代码中使用**
也可以将`ManifestEditor.jar`文件导入到Android或Java工程中使用，接入方法为：
```
    ModificationProperty property = new ModificationProperty();

    property.addManifestAttribute(new AttributeItem(NodeValue.Manifest.PACKAGE, "wind.new.pkg.name").setNamespace(null))
                .addManifestAttribute(new AttributeItem(NodeValue.Manifest.VERSION_CODE, 1))
                .addManifestAttribute(new AttributeItem(NodeValue.Manifest.VERSION_NAME, "1123"))
                .addUsesPermission("android.permission.READ_EXTERNAL_STORAGE")
                .addUsesPermission("android.permission.WRITE_EXTERNAL_STORAGE")
                .addMetaData(new ModificationProperty.MetaData("aa", "11"))
                .addMetaData(new ModificationProperty.MetaData("aa", "22"))
                .addApplicationAttribute(new AttributeItem(NodeValue.Application.DEBUGGABLE, true))
                .addApplicationAttribute(new AttributeItem(NodeValue.Application.NAME, "my.app.name.MyTestApplication"))
                .addApplicationAttribute(new AttributeItem("appComponentFactory", "my.app.name.MyAppComponentFactory"));

    String inputManifestFilePath = "../../AndroidManifest_old.xml";
    String outputManifestFilePath = "../../AndroidManifest.xml";

    // 处理manifest文件方法
    FileProcesser.processManifestFile(inputManifestFilePath, outputManifestFilePath, property);

    String inputApkFilePath = "../../original_old.apk";
    String outputApkFilePath = "../../new_build_unsigned.apk";

    // 处理得到的apk是未签名的，需要自行签名使用
    FileProcesser.processApkFile(inputApkFilePath, outputApkFilePath, property);
```
# **License**
Originally forked from [axml](https://github.com/Sable/axml).
```
Copyright 2020, WindySha

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work 
except in compliance with the License. You may obtain a copy of the License in the 
LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the
License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
ANY KIND, either express or implied. See the License for the specific language governing
permissions and limitations under the License.
```

