
#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <sys/file.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <assert.h>
#include <fcntl.h>
#include "common.h"

#define JNIREG_CLASS "com/bossy/alive/KANative"//指定要注册的类

extern "C" {

/**
 * function name : create_and_wait_file
 * 创建file3文件，并且循环等待file4文件创建成功，创建成功后移除file2
 * @param file3
 * @param file4
 * @return
 */
bool create_and_wait_file(const char *file3, const char *file4) {
    if (open(file3, 0) == -1) {
        open(file3, O_CREAT, S_IRUSR | S_IWUSR);
    }
    while (open(file4, 0) == -1) {
        usleep(0x3E8u);
    }
    remove(file4);
    return LOGD("Watched >>>>OBSERVER<<<< has been ready2...");
}

/**
 * function name : lock_file
 * 打开文件，并且使用排斥锁给文件加锁,此操作为使进程处于阻塞状态
 * @param file
 * @return
 */
bool lock_file(const char *file) {
    LOGD("start try to lock file : %s", file);
    int fd = open(file, 0);
    if (fd == -1) {
        fd = open(file, O_CREAT, S_IRUSR);
    }
    if (flock(fd, LOCK_EX) == -1) {
        LOGD("lock file failed : %s", file);
        return 0;
    }
    LOGD("lock file success : %s", file);
    return 1;
}

/**
 * function name : notify_process_dead
 * 进程已经死亡，通知java层，并且
 * @param env
 * @return
 */
bool notify_process_dead(JNIEnv *env) {
    LOGD("java callback start");
    jclass processClass = env->FindClass(JNIREG_CLASS);
    jmethodID notifyDeadMethod = env->GetStaticMethodID(processClass, "notifyDead",
                                                        "()V");
    env->CallStaticVoidMethod(processClass, notifyDeadMethod);
    LOGD("java callback end");
    int pid = getpid();
    if ((int) pid >= 1) {
        // 向pid为首的进程组发送结束进程信号
        return killpg(pid, SIGTERM/*15*/);
    }
    return pid;
}

/**
 * function name : lock_and_monitor
 * 锁定file1文件，并在file2文件上等待另一个进程的文件锁
 * @param env
 * @param file1
 * @param file2
 * @param file3
 * @param file4
 * @return
 */
bool lock_and_monitor(JNIEnv *env, const char *file1, const char *file2, const char *file3,
                      const char *file4) {
    // 锁定file1文件，如果失败，则尝试2次
    if ((lock_file(file1) || (LOGD("Persistent lock myself failed and try again as 1 times")
            , usleep(0x2710u)
            , lock_file(file1)))
        || (lock_file(file1) || (LOGD("Persistent lock myself failed and try again as 2 times")
            , usleep(0x2710u)
            , lock_file(file1)))) {
        create_and_wait_file(file3, file4);
        // 尝试给file2文件加锁，并且阻塞进程
        int result = lock_file(file2);
        if (result) {
            LOGD("Watch >>>>DAEMON<<<<< Daed !!");
            remove(file3);
            return notify_process_dead(env);
        }
    } else {
        LOGD("Persistent lock myself failed and try again as 3 times");
        usleep(0x2710u);
        return LOGD("Persistent lock myself failed and exit");
    }
    return 0;
}

/**
 * function name : monitor
 * JNI函数，每个进程调用次函数启动进程监控功能
 * @param env
 * @param jobj
 * @param file1
 * @param file2
 * @param file3
 * @param file4
 * @param sub_process_name
 * @return
 */
bool monitor(JNIEnv *env, jclass jobj, jstring file1, jstring file2, jstring file3, jstring file4,
             jstring sub_process_name) {
    if (!file1 || !file2 || !file3 || !file4) {
        return JNI_FALSE;
    }
    const char *str_file1;
    const char *str_file2;
    const char *str_file3;
    const char *str_file4;
    char filename1[256] = {0};
    char filename2[256] = {0};
    char filename3[256] = {0};
    char filename4[256] = {0};

    str_file1 = env->GetStringUTFChars(file1, 0);
    str_file2 = env->GetStringUTFChars(file2, 0);
    str_file3 = env->GetStringUTFChars(file3, 0);
    str_file4 = env->GetStringUTFChars(file4, 0);

    int fork_pid = fork();

    if ((fork_pid & 0x80000000) != 0) {
        exit(0);
    }
    // 子进程
    if (!fork_pid) {
        int fork_pid2 = fork();
        if ((fork_pid2 & 0x80000000) == 0) {
            // 父进程退出
            if (fork_pid2) {
                exit(0);
            }
            int pid = getpid();
            LOGD("my pid : %d", pid);
            strcpy(filename1, str_file1);
            strcpy(&filename1[strlen(filename1)], "-c");

            strcpy(filename2, str_file2);
            strcpy(&filename2[strlen(filename2)], "-c");

            strcpy(filename3, str_file3);
            strcpy(&filename3[strlen(filename3)], "-c");

            strcpy(filename4, str_file4);
            strcpy(&filename4[strlen(filename4)], "-c");

            FILE *p_file1 = fopen(filename1, "ab+");
            if (p_file1) {
                fclose(p_file1);
            }
            FILE *p_file2 = fopen(filename2, "ab+");
            if (p_file2) {
                fclose(p_file2);
            }
            jclass processClass = env->FindClass("android/os/Process");
            jmethodID setArgV0Method = env->GetStaticMethodID(processClass, "setArgV0",
                                                              "(Ljava/lang/String;)V");
            jstring process_name = sub_process_name;
            if (process_name == NULL) {
                const char *p_name = "wifi_process";
                process_name = env->NewStringUTF(p_name);
            }
            env->CallStaticVoidMethod(processClass, setArgV0Method, process_name);

            setsid();
            chdir("/");
            umask(0);
            int ret = lock_and_monitor(env, filename1, filename2, filename3, filename4);
            env->ReleaseStringUTFChars(file1, str_file1);
            env->ReleaseStringUTFChars(file2, str_file2);
            env->ReleaseStringUTFChars(file3, str_file3);
            env->ReleaseStringUTFChars(file4, str_file4);
            return ret;
        }
    }
    waitpid(fork_pid, 0, 0);
    int result = lock_and_monitor(env, str_file1, str_file2, str_file3, str_file4);
    env->ReleaseStringUTFChars(file1, str_file1);
    env->ReleaseStringUTFChars(file2, str_file2);
    env->ReleaseStringUTFChars(file3, str_file3);
    env->ReleaseStringUTFChars(file4, str_file4);
    return result;
}

/**
* Table of methods associated with a single class.
*/
static JNINativeMethod gMethods[] = {
        {"nativeMonitor", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Z", (void *) monitor}
};

static int registerNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

static int registerNatives(JNIEnv *env) {
    const char *jni_class_name = JNIREG_CLASS;
    LOGV("native name : %s", jni_class_name);
    if (!registerNativeMethods(env, jni_class_name, gMethods,
                               sizeof(gMethods) / sizeof(gMethods[0]))) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    if (!registerNatives(env)) {//注册
        return -1;
    }
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;
    return result;
}
}