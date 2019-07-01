# uploadplunin
Android 自定义 gradle plugin 实现自定打包上传apk到指定服务器

1、使用方式修改buildSrc目录下的	UploadPlugin.groovy 中47行代码 替换成自己的服务器地址

	uploadFile(file, "http://127.0.0.1:8080/bb/upload2")

2、执行 ./gradlew uploadApk  或者在gradle右侧视图中找到 app->Tasks->other->uploadApk 单击执行
