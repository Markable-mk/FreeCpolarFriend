echo "1 BUILD"
docker build --build-arg JAR_FILE=freeCpolarFrend-1.0-SNAPSHOT.jar -t free-cpolar-frend:latest .
echo "2 TAG"
docker tag free-cpolar-frend:latest registry.cn-hangzhou.aliyuncs.com/mk-dev/free-cpolar-frend:latest
echo "3 PUSH"
docker push registry.cn-hangzhou.aliyuncs.com/mk-dev/free-cpolar-frend:latest