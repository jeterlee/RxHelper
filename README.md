[![](https://jitpack.io/v/jeterlee/RxHelper.svg)](https://jitpack.io/#jeterlee/RxHelper)


# RxHelper
项目说明：Rx帮助类，封装了一些常用Rx系列的帮助类，为了更方便的使用一些Rx系列。


## 一、开发环境
|开发工具|开发语言|SDK版本|JDK版本|
|:-----:|:-----:|:-----:|:-----:|
|AndroidStudio3.0|JAVA|27|1.8|


## 二、使用说明
- rxhelper主要实现了subscribe订阅，并且会根据组件的生命周期销毁而销毁，从而防止使用RxJava带来内存泄露。
使用前需要先注入rxhelper，即在Activity或Application中注入RxHelper，本质注入RxLifecycle。
有两种情况：1. 若定义了BaseActivity，在BaseActivity的onCreate方法里注入RxHelper。2. 若已继承Application的操作，也可以在onCreate方法中注入。
```
// 注入rxhelper的方法，即调用injectRxHelper
RxHelper.injectRxHelper(this);
```

- rxpermissionshelper主要实现权限的申请，直接调用对应的权限方法（权限申请简单明了）。
```
// 例如调用摄像头权限
mRxPermissionsHelper.launchCamera(new IRequestPermissionCallback());
```

**注：详细说明可见代码。**


## 三、依赖配置
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```
allprojects {
 	repositories {
 		...
 		maven { url "https://jitpack.io" }
 	}
 }
```

Step 2. Add the dependency
```
implementation 'com.github.jeterlee.RxHelper:rxhelper:v1.0.0'
implementation 'com.github.jeterlee.RxHelper:rxpermissionshelper:v1.0.0'
```


## 四、更新记录
- 2017-11-30
更新了rxhelper和rxpermissionshelper。


## 五、参考项目
待定


# License
```
Copyright (c) 2017, The Jeterlee authors 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
