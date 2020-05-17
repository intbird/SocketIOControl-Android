#include <jni.h>
#include <string>
#include <stdlib.h>
#include <iostream>

extern "C"
JNIEXPORT void JNICALL
Java_com_intbird_soft_socketiolib_cnative_SocketIOSender_sendMessage(JNIEnv *env, jobject thiz,
                                                                     jstring message) {

    const char *className = "com/intbird/soft/socketiolib/cnative/SocketIOSender";
    jclass clz = env->FindClass(className);
    if (clz == 0) {
        return;
    }

    const char *methodName = "receiveMessage";
    jmethodID jmethodId = env->GetMethodID(clz, methodName, "(Ljava/lang/String;)V");
    if (jmethodId == 0) {
        return;
    }

    const char *result = "callback result";
    env->CallVoidMethod(thiz, jmethodId, env->NewStringUTF(result));
}