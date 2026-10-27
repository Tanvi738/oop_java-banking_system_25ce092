#!/usr/bin/env bash
# ==============================================================================
# Banking Management System - Build & Run Script (macOS / Linux)
# Student: Tanvi Patel (25CE092)
# ==============================================================================

set -e

# Detect Java command
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/javac" ]; then
    JAVAC_CMD="$JAVA_HOME/bin/javac"
    JAVA_CMD="$JAVA_HOME/bin/java"
elif command -v /Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/javac &>/dev/null; then
    JAVAC_CMD="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/javac"
    JAVA_CMD="/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java"
elif command -v javac &>/dev/null; then
    JAVAC_CMD="javac"
    JAVA_CMD="java"
else
    echo "Error: Java Development Kit (JDK) not found. Please install JDK 17 or higher."
    exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR"
if [ ! -d "$ROOT_DIR/src" ]; then
    ROOT_DIR="$(dirname "$SCRIPT_DIR")"
fi

cd "$ROOT_DIR"

echo "--------------------------------------------------------"
echo " Compiling Java Banking System..."
echo " Compiler: $($JAVAC_CMD -version 2>&1)"
echo "--------------------------------------------------------"

mkdir -p bin
mkdir -p data

# Compile all source files
$JAVAC_CMD -d bin $(find src -name "*.java")

echo " Compilation successful!"
echo " Starting application..."
echo "--------------------------------------------------------"
echo ""

# Run main class
$JAVA_CMD -cp bin com.bank.Main
