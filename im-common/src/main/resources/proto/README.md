## 目录说明
该目录下定义了项目所需的proto文件


生成命令
```shell
protoc --java_out=要生成的目标文件夹 proto文件
```
例如

```shell
protoc --java_out=. proto/MsgProto.proto
```
