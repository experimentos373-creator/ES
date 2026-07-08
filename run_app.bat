@echo off
echo ==================================================
echo   A iniciar a Aplicacao Gestao WC 2026 (GUI)...
echo ==================================================
set JAVA_HOME=C:\Users\paulo\.jdks\openjdk-24.0.1
set JAVA_TOOL_OPTIONS=-Xmx512m -XX:+UseSerialGC
set MAVEN_OPTS=-Xmx512m -XX:+UseSerialGC
cd /d "%~dp0projeto_java"
call "C:\Users\paulo\.antigravity-ide\extensions\oracle.oracle-java-26.0.0-universal\nbcode\java\maven\bin\mvn.cmd" javafx:run
pause
