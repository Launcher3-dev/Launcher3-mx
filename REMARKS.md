# 常见问题备注

## 目录
* [解析配置信息错误](#解析配置信息错误)


## 解析配置信息错误
#### 1.修改xml文件中的命名空间，导致解析hotseat配置信息没有的问题
由于导入Android Studio后，提示我修改命名空间：
```
// 原始命名空间
<favorites xmlns:launcher="http://schemas.android.com/apk/res-auto/com.android.launcher3">
// 提示修改后的命名空间
<favorites xmlns:launcher="http://schemas.android.com/apk/res-auto">
```
文件包括：
```
default_workspace_3x3.xml
default_workspace_4x4.xml
default_workspace_5x5.xml
default_workspace_6x6.xml
device_profiles.xml
dw_phone_hotseat.xml
dw_tablet_hotseat.xml
```
导致hotseat中的应用信息一直加载失败，后来跟踪Launcher代码知道，在
```
AutoInstallsLayout.getAttributeValue(XmlResourceParser parser, String attribute)
AutoInstallsLayout.getAttributeResourceValue(XmlResourceParser parser, String attribute, int defaultValue)
```
两个方法中用到了命名空间
```
parser.getAttributeValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", attribute);

parser.getAttributeResourceValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", attribute, defaultValue);
```
解析的时候找不到对应的命名空间导致加载失败，修改办法就是统一命名空间，全部修改为：
```
"http://schemas.android.com/apk/res-auto"
```