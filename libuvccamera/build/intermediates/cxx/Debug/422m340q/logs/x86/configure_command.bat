@echo off
"D:\\MYSDK\\ndk\\25.1.8937393\\ndk-build.cmd" ^
  "NDK_PROJECT_PATH=null" ^
  "APP_BUILD_SCRIPT=D:\\ANDROIDWORK_SPACE\\ILook\\libuvccamera\\src\\main\\jni\\Android.mk" ^
  "NDK_APPLICATION_MK=D:\\ANDROIDWORK_SPACE\\ILook\\libuvccamera\\src\\main\\jni\\Application.mk" ^
  "APP_ABI=x86" ^
  "NDK_ALL_ABIS=x86" ^
  "NDK_DEBUG=1" ^
  "APP_PLATFORM=android-19" ^
  "NDK_OUT=D:\\ANDROIDWORK_SPACE\\ILook\\libuvccamera\\build\\intermediates\\cxx\\Debug\\422m340q/obj" ^
  "NDK_LIBS_OUT=D:\\ANDROIDWORK_SPACE\\ILook\\libuvccamera\\build\\intermediates\\cxx\\Debug\\422m340q/lib" ^
  "APP_SHORT_COMMANDS=false" ^
  "LOCAL_SHORT_COMMANDS=false" ^
  -B ^
  -n
