plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
//    implementation("log4j:log4j:1.2.17")
    implementation("org.slf4j:slf4j-api:2.0.9")
    // slf4j 提供程序（或绑定）用于简单实现的绑定/提供程序，将所有事件输出到 System.err。仅打印 INFO 及更高级别的消息。
//    implementation("org.slf4j:slf4j-simple:2.0.9")
    implementation("org.slf4j:slf4j-log4j12:2.0.9")
//    implementation("org.slf4j:slf4j-reload4j:2.0.9")
//    implementation("org.slf4j:slf4j-jdk14:2.0.9")


    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}