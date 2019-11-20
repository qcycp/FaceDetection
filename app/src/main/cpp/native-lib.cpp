#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_foxconn_liveness_FDdemoActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "HAHAHA";
    return env->NewStringUTF(hello.c_str());
}
