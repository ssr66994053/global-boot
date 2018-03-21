创建项目

    mvn archetype:generate  -DarchetypeGroupId=com.yiji.archetype  -DarchetypeArtifactId=yiji-boot-archetype -DarchetypeVersion=1.2-SNAPSHOT -DarchetypeRepository=http://192.168.45.35:8081/nexus/content/groups/public/ -Dwebport=8081  -DgroupId=com.yiji.cs -DartifactId=cs -Dversion=1.0

参数说明:

    -Dwebport 指定web容器端口
    -DgroupId 指定group名,我们的项目应该意com.yiji开头
    -DartifactId 项目名
    -Dversion 项目版本号

本地测试:

    mvni
    mvn archetype:generate  -DarchetypeGroupId=com.yiji.archetype  -DarchetypeArtifactId=yiji-boot-archetype -DarchetypeVersion=1.2-SNAPSHOT -DarchetypeCatalog=local -Dwebport=8081  -DgroupId=com.yiji.cs -DartifactId=cs -Dversion=1.0


