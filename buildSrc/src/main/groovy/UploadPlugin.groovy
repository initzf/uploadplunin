import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileTreeElement

class UploadPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("------>进来自定义插件")

        //第一种方式  ./gradlew uploadApk
        project.task("uploadApk") {
            println("第一步 执行了uploadApk脚本")
        } dependsOn("assembleDebug") {
            println("第二步 assembleDebug打包")
        } doLast {
            println("第三步 上传")
            findFile(project)
        }


        //第二种方式执行默认的打包  ./gradlew assembleDebug
        /*Task task = project.task("uploadApk") {
        } doLast {
            println("第二步 上传")
            findFile(project)
        }
        project.tasks.whenObjectAdded { Task theTask ->
            if (theTask.name == 'assembleDebug') {
                println("第一步 打包")
                theTask.dependsOn(task)
            }
        }*/
    }

    /**
     * 查找文件
     * @param project
     */
    void findFile(Project project) {
        project.fileTree("build/outputs/apk") { FileTree fileTree ->
            fileTree.visit { FileTreeElement element ->
                File file = element.file
                if (file.name.endsWith(".apk")) {
                    println(file.absolutePath)
                    //uploadFile(file, "http://127.0.0.1:8080/bb/upload2")
                }
            }
        }
    }


    /*
     * 上传代码拷贝与这篇博客
     * https://blog.csdn.net/u012527802/article/details/51153014
     *
     * 我这边测试自己搭了一个springboot的服务器使用,注意需要设置上传文件的限定大小，它们默认是1m大小.
     *              @RequestMapping("/upload2")
     *              public String register2(@RequestParam("file") MultipartFile multipartFile){
     *                  //code
     *              }
     *
     * android上传文件到服务器
     * @param file 需要上传的文件
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */

    void uploadFile(File file, String RequestURL) {
        String BOUNDARY = UUID.randomUUID().toString()  //边界标识   随机生成
        String PREFIX = "--", LINE_END = "\r\n"
        String CONTENT_TYPE = "multipart/form-data"   //内容类型

        try {
            URL url = new URL(RequestURL)
            HttpURLConnection conn = (HttpURLConnection) url.openConnection()
            conn.setDoInput(true)  //允许输入流
            conn.setDoOutput(true) //允许输出流
            conn.setUseCaches(false)  //不允许使用缓存
            conn.setRequestMethod("POST")  //请求方式
            conn.setRequestProperty("Charset", "utf-8")  //设置编码
            conn.setRequestProperty("connection", "keep-alive")
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY)
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                OutputStream outputSteam = conn.getOutputStream()

                DataOutputStream dos = new DataOutputStream(outputSteam)
                StringBuffer sb = new StringBuffer()
                sb.append(PREFIX)
                sb.append(BOUNDARY)
                sb.append(LINE_END)
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的   比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINE_END)
                sb.append("Content-Type: application/octet-stream; charset = utf-8" + LINE_END)
                sb.append(LINE_END)
                dos.write(sb.toString().getBytes())
                InputStream is = new FileInputStream(file)
                byte[] bytes = new byte[1024]
                int len = 0
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len)
                }
                is.close()
                dos.write(LINE_END.getBytes())
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes()
                dos.write(end_data)
                dos.flush()
                /**
                 * 获取响应码  200=成功
                 * 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode()
                println("状态----->" + res)
                if (res == HttpURLConnection.HTTP_OK) {
                    def inputstream = conn.getInputStream()
                    println(inputstream.text)

                    inputstream.close()
                }
                conn.disconnect()
            }
        } catch (MalformedURLException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

}