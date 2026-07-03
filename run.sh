#!/bin/bash
# Job Platform Management System — run script
# Requires Java 17 or higher

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Compile if out/ is missing or stale
if [ ! -d "out" ] || [ "$(find src -name '*.java' -newer out -print -quit 2>/dev/null)" ]; then
    echo "Compiling..."
    mkdir -p out
    find src -name "*.java" > /tmp/jp_sources.txt
    javac -d out -sourcepath src/main/java @/tmp/jp_sources.txt
    if [ $? -ne 0 ]; then
        echo "Compilation failed."
        exit 1
    fi
    echo "Compiled successfully."
fi

mkdir -p data
java -jar JobPlatform.jar 2>/dev/null || java -cp out jobplatform.Main
