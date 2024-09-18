# 项目介绍


# 部署
## 1 docker方式部署
- 1 修改配置文件中的用户名密码token等，然后打包

- 2 将jar包和dockerfile丢入一个文件夹

- 3 执行构建命令
```shell
docker build --build-arg JAR_FILE=freeCpolarFrend-1.0-SNAPSHOT.jar -t free-cpolar-frend:latest .
```

- 4 docker部署
```shell
docker run -d --name xm-cpolar free-cpolar-frend:latest
```