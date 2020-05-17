#include <jni.h>
#include <android/log.h>
#include <string>
#include <stdlib.h>
#include <iostream>

#define APP_NAME "socket-io"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, APP_NAME, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_INFO, APP_NAME, __VA_ARGS__))

char *formatTime();

extern "C" {
    JNIEXPORT void JNICALL
    Java_com_intbird_soft_socketiolib_cnative_Logger_d(JNIEnv *env, jobject thiz, jstring jtag,
                                                       jstring jMessage) {
        const char *tag = env->GetStringUTFChars(jtag, NULL);
        LOGD(tag);
    }

    extern "C"
    JNIEXPORT void JNICALL
    Java_com_intbird_soft_socketiolib_cnative_Logger_e(JNIEnv *env, jobject thiz, jstring jtag,
                                                       jstring jMessage) {
        const char *tag = env->GetStringUTFChars(jtag, NULL);
        LOGE(tag);
    }
}

char *formatTime() {
    static char time[20] = "";
    strcat(time, __DATE__);
    strcat(time, "\t");
    strcat(time, __TIME__);
    strcat(time, "\t");
    return time;
}
