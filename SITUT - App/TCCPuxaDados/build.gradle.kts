// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google() // Adiciona o repositório do Google
        mavenCentral() // Adiciona o repositório Maven Central
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // Versão do plugin do Google Services mais recente
        // Outras dependências do classpath podem ser adicionadas aqui
    }
}

plugins {
    id("com.android.application") version "8.6.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
}
