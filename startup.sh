cp opencv_java331.dll /tmp
cp resources/CarsDrivingUnderBridge.mp4 /tmp
mvn clean install
java jar target/cars-monitoring-step2-1.0-SNAPSHOT.jar