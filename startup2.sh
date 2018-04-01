echo In startap v2
cp opencv_java331.dll /tmp
cp src/main/resources/CarsDrivingUnderBridge.mp4 /tmp
java -Dserver.port=$PORT $JAVA_OPTS -jar target/*.jar -Djava.library.path="/tmp/"
