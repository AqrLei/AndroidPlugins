>[McImage](https://github.com/smallSohoSolo/McImage)
>[Webp Guides](https://developers.google.com/speed/webp/docs/using)
- 属性说明

| 属性               | 说明         |
|:-------------------|:------------|
| enableWhenDebug    | debug时是否执行，默认为true |
| enableWhenRelease  | release时是否执行，默认为false|
| multiThread        | 是否采用多线程执行，默认为false |
| toolsDir           | 工具所在的根目录，默认为项目根目录 [Tools](../tools)|
| optimizeType       | 图片转化类型，默认为压缩(**compress**),可设置为**webpConvert**   |
| whiteList          | 白名单，其中的图片文件不会进行处理 |
| singleCompress     | 同一个文件是否只压缩一次，默认为true|



- 使用说明

 **最低支持Gradle版本5.6.4**
 
1. Groovy

```Groovy
   classpath "com.aqrlei.plugin:ImageConvert:latest"

   apply plugin: "com.aqrlei.plugin.imageConvert"

   imageConvert {
          enableWhenDebug = true 
          enableWhenRelease = false 
          multiThread = true
          singleCompress = true
          toolsDir = project.rootDir.path
          optimizeType = "compress"

          whiteList = ["ic-launcher.png", "ic_launcher_round.png"]

   }
```
2. Kotlin

```Kotlin
    classpath("com.aqrlei.plugin:ImageConvert:latest")

   import com.aqrlei.plugin.imageconvert.ImageConvertConfig.OptimizeType

   plugins {
       id("com.aqrlei.plugin.imageConvert")
   }

   imageConvert {
          enableWhenDebug = true
          enableWhenRelease = false
          multiThread = true
          singleCompress = true
          toolsDir = project.rootDir.path
          optimizeType = OptimizeType.COMPRESS
          whiteList = arrayOf("ic-launcher.png", "ic_launcher_round.png")
   }
```

- 生成的文件说明
  
  生成的文件在**`${project.projectDir}/imageconvert/`**目录下
  
  | 文件名               | 说明         |
  |:-------------------|:------------|
  | image_compress.txt    | 记录被压缩过的文件相对路径，第一次执行任务后生成，如果图片设置只能压缩一次，后续执行任务时会过滤掉与之匹配的文件 |
  | image_compress_escape.txt  | 记录无需进行图片压缩的文件相对路径，第一次执行任务后生成，后续任务会过滤掉与之匹配的的文件|
  | image_convert_detail.txt   | 记录图片压缩及转化详细信息，包含转化减少的体积及转化耗时 |
  | image_webp_convert_escape.txt   | 记录无需进行webp格式转化的文件相对路径，第一次执行任务后生成，后续任务会过滤掉与之匹配的的文件|
  | image_webp_convert.txt   | 记录被转化成webp的图片相对路径的文件|
  | image_white.txt       | 记录白名单文件的相对路径，与项目中设置的白名单文件比对   |