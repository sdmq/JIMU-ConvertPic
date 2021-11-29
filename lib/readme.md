**将Jar包导入本地Maven库** 

本服务中，需要使用JAI包。但是目前无法从Maven在线库中下载此包，所以需要将已经下载好的jar包导入到本地Maven库中。

首先，本地需要安装Maven，并且设置好本地Maven库。本例中，本地安装了Maven3.5.4，本地Maven库路径为 D:/MavenRep

将本文件夹（lib）中的两个Jar包复制到任意一个路径中。比如，D:\cvtest 文件夹中。

执行以下Maven安装命令：

1、将jai_core加入到本地Maven库

mvn install:install-file -Dfile=D:\cvtest\jai_core-1.1.2-beta.jar -DgroupId=javax.media -DartifactId=jai_core -Dversion=1.1.2-beta -Dpackaging=jar

2、将jai_codec加入到本地Maven库

mvn install:install-file -Dfile=D:\cvtest\jai_codec-1.1.3.jar -DgroupId=javax.media -DartifactId=jai_codec -Dversion=1.1.3 -Dpackaging=jar

（Maven命令说明：mvn install:install-file -Dfile=jar包的位置 -DgroupId=上面的groupId -DartifactId=上面的artifactId -Dversion=上面的version -Dpackaging=jar）

系统即会自动将jar包复制到 D:/MavenRep 下的子文件夹中，并且自动生成相关配置属性文件。

mvn命令提示安装成功即可。



第二步，修改idea或Eclipse中Maven的配置。

将原有的内置的Maven改为外部安装的Maven路径。系统一般会自动获取到已经配置好的Maven库的路径。

后续，更新一下工程即可。