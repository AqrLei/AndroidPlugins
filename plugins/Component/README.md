
- 属性说明

1. 在**project.projectDir**的目录下**local.properties** 中设置, 不上传到远程
2. 在project.projectDir的目录下**component.properties** 中设置，上传到远程
3. 在**project.rootProject.projectDir**的目录下**local.properties** 中设置**component.asAAR**，不上传远程


| 属性               | 说明         |
|:-------------------|:------------|
| component.asAAR    | 作为library,这个属性只能在项目根目录的**local.properties**下设置会对所有非mainApp有效， |
| component.alwaysLib  | 一直作为一个library|
| component.mainApp        | 一直是application |
| component.applicationId  | 设置之后会取代默认的 applicationId  |
| component.debugSrcDir    | 用于调试的源文件所在的目录,默认值"src/main/debug/" |



- 使用说明

 **最低支持Gradle版本5.6.4**
 
1. Groovy

```Groovy
   classpath "com.mockuai.plugin:component:latest"

   apply plugin: "com.mockuai.plugin.component"

```
2. Kotlin

```Kotlin
    classpath("com.mockuai.plugin:component:latest")

   plugins {
       id("com.mockuai.plugin.component")
   }
```

3. Properties
module目录下local.properties 或 component.properties
```
component.alwaysLib = false
component.mainApp = false
component.debugSrcDir = src/main/debug/
component.applicationId = 

```

项目根目录下的local.properties
```
component.asAAR = false

```

- 生成的文件说明
  
  生成的文件为**`${project.buildDir}/component_record.txt/`**, 记录了build过程中属性对应的值
 