Http协议编码解码

1. http文件服务器
http://www.cnblogs.com/hapjin/p/5364416.html

2. jibx（javabean与xml的转换）
<pre><code>
    1)、首先，写好javabean
    2)、运行ant，执行build.xml里的bindgen任务（
            其他任务是研究过程的产物，可做参考；
            另外，需要使用1.3.1版本一开始使用1.2.6的版本时执行报错
    ）
    3)、使用jibx的maven插jibx-maven-plugin，编译时自动生成对应的jibx动态类
</code></pre>