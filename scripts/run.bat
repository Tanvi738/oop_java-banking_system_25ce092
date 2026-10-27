@echo off
REM ==============================================================================
REM Banking Management System - Build & Run Script (Windows)
REM Student: Tanvi Patel (25CE092)
REM ==============================================================================

echo --------------------------------------------------------
echo  Compiling Java Banking System...
echo --------------------------------------------------------

if not exist bin mkdir bin
if not exist data mkdir data

javac -d bin -sourcepath src src/com/bank/Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Ensure JDK is in your PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo  Compilation successful!
echo  Starting application...
echo --------------------------------------------------------
echo.

java -cp bin com.bank.Main
pause
