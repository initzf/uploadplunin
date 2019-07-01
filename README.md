# uploadplunin
Android 自定义 gradle plugin 实现自定打包上传apk到指定服务器

1、先修改buildSrc目录下的 UploadPlugin.groovy 中47行代码 替换成自己的服务器地址

	uploadFile(file, "http://127.0.0.1:8080/bb/upload2")

2、执行 ./gradlew uploadApk  或者在gradle右侧视图中找到 app->Tasks->other->uploadApk 单击执行

3、使用第二种方式实现打包上传、当执行了 assembleDebug 脚本,打包完成后会自动触发uploadApk脚本上传到服务器

```
Task task = project.task("uploadApk") {
      } doLast {
          println("第二步 上传")
          findFile(project)
      }
      project.tasks.whenObjectAdded { Task theTask ->
          if (theTask.name == 'assembleDebug') {
              println("第一步 打包")
              theTask.dependsOn(task)
          }
      }
```
