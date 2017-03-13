export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK8
export PATH=$JDK7/bin:$GRADLE_HOME/bin:$PATH
export GRADLE_HOME=/data/rdm/apps/gradle/gradle-2.14.1
export PATH=$JDK7/bin:$GRADLE_HOME/bin:$PATH
PROJECT_PATH=iLiveSDK_Demos/
PACKAGE_NAME=iLiveSDK_Demos_${MajorVersion}.${MinorVersion}.${FixVersion}
PACKAGE_ROOT=../../bin/
PACKAGE_PATH=${PACKAGE_ROOT}/${PACKAGE_NAME}
gradle build


cp app/build/outputs/apk/*.apk bin