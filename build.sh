export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK8
export GRADLE_HOME=/data/rdm/apps/gradle/gradle-2.14.1
export PATH=$JDK8/bin:$GRADLE_HOME/bin:$PATH

set -x
PROJECT_PATH=iLiveSDK_Demos/

PACKAGE_NAME=iLiveSDK_Demos_${MajorVersion}.${MinorVersion}.${FixVersion}
PACKAGE_ROOT=../../bin/
PACKAGE_PATH=${PACKAGE_ROOT}/${PACKAGE_NAME}

export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK8
export GRADLE_HOME=/data/rdm/apps/gradle/gradle-2.14.1
export PATH=$JDK7/bin:$GRADLE_HOME/bin:$PATH
gradle build
::cp app/build/outputs/apk/*.apk bin

cp app/build/outputs/apk/app-debug.apk bin/iLiveSDK_Demos-debug-${MajorVersion}.${MinorVersion}.${FixVersion}.apk