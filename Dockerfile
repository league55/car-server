FROM picoded/ubuntu-openjdk-8-jdk

#openCV part
ARG OPENCV_VERISON="3.3.1"
# install dependencies
RUN apt-get update && apt-get install -y libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev python-dev python-numpy libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libjasper-dev libdc1394-22-dev
RUN apt-get install -y curl build-essential checkinstall cmake && \
    curl -sL https://github.com/Itseez/opencv/archive/$OPENCV_VERISON.tar.gz | tar xvz -C /tmp && \
    mkdir -p /tmp/opencv-$OPENCV_VERISON/build && \
    cd /tmp/opencv-$OPENCV_VERISON/build && \
    cmake -DWITH_FFMPEG=OFF -DWITH_OPENEXR=OFF -DBUILD_TIFF=OFF -DWITH_CUDA=OFF -DWITH_NVCUVID=OFF -DBUILD_PNG=OFF .. && \
    make && make install && \
    echo "/usr/local/lib" > /etc/ld.so.conf.d/opencv.conf && ldconfig && \
    cd / && apt-get remove --purge -y curl build-essential checkinstall cmake && \
    apt-get autoclean && apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# configure
RUN ln /dev/null /dev/raw1394 # hide warning - http://stackoverflow.com/questions/12689304/ctypes-error-libdc1394-error-failed-to-initialize-libdc1394

#javaFX
RUN apt-get update && apt-get install -y --no-install-recommends openjfx && rm -rf /var/lib/apt/lists/*

#Java part
VOLUME /tmp
EXPOSE 8080
COPY ./target/cars-monitoring-step2-1.0-SNAPSHOT.jar /usr/src/hola/
WORKDIR /usr/src/hola


ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","cars-monitoring-step2-1.0-SNAPSHOT.jar"]






