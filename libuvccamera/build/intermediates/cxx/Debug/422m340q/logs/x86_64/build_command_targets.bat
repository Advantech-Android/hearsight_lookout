@echo off
"D:\\MYSDK\\ndk\\25.1.8937393\\ndk-build.cmd" ^
  "NDK_PROJECT_PATH=null" ^
  "APP_BUILD_SCRIPT=D:\\ANDROIDWORK_SPACE\\AndroidStudioProjects\\libuvccamera\\src\\main\\jni\\Android.mk" ^
  "NDK_APPLICATION_MK=D:\\ANDROIDWORK_SPACE\\AndroidStudioProjects\\libuvccamera\\src\\main\\jni\\Application.mk" ^
  "APP_ABI=x86_64" ^
  "NDK_ALL_ABIS=x86_64" ^
  "NDK_DEBUG=1" ^
  "APP_PLATFORM=android-19" ^
  "NDK_OUT=D:\\ANDROIDWORK_SPACE\\AndroidStudioProjects\\libuvccamera\\build\\intermediates\\cxx\\Debug\\422m340q/obj" ^
  "NDK_LIBS_OUT=D:\\ANDROIDWORK_SPACE\\AndroidStudioProjects\\libuvccamera\\build\\intermediates\\cxx\\Debug\\422m340q/lib" ^
  UVCCamera ^
  jpeg-turbo212 ^
  usb1.0 ^
  uvc
