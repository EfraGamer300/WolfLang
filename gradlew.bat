@echo off
set GRADLE_OPTS=-Dorg.gradle.daemon=false
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot
"%~dp0gradle-dist\gradle-8.5\bin\gradle.bat" %*
