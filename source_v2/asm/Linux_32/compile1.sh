clear
fasm LINUX32JNI.asm
gcc -shared -o libLINUX32JNI.so LINUX32JNI.o -z noexecstack


