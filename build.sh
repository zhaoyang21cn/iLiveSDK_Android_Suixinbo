export ANDROID_HOME=$ANDROID_SDK
export JAVA_HOME=$JDK8
export GRADLE_HOME=/data/rdm/apps/gradle/gradle-2.14.1
export PATH=$JDK8/bin:$GRADLE_HOME/bin:$PATH

gradle build
::cp app/build/outputs/apk/*.apk bin

cp app/build/outputs/apk/app-debug.apk bin/ILiveSuixinbo-debug-${MajorVersion}.${MinorVersion}.${FixVersion}.apk
