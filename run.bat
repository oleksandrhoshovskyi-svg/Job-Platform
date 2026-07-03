@echo off
REM Job Platform Management System — Windows run script
REM Requires Java 17 or higher on PATH

cd /d "%~dp0"

REM Compile if out\ is missing
if not exist "out\" (
    echo Compiling...
    mkdir out
    dir /s /b src\*.java > sources.txt
    javac -d out -sourcepath src\main\java @sources.txt
    if errorlevel 1 (
        echo Compilation failed.
        pause
        exit /b 1
    )
    echo Compiled successfully.
)

if not exist "data\" mkdir data

REM Try jar first, fall back to class files
if exist "JobPlatform.jar" (
    java -jar JobPlatform.jar
) else (
    java -cp out jobplatform.Main
)
