clear
fasm LINUX64JNI.asm
gcc -shared -o libLINUX64JNI.so LINUX64JNI.o -z noexecstack


