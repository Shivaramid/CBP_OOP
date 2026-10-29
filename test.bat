@echo off
if not exist bin\mccgrs\SystemTest.class (
    echo [INFO] Binaries not found. Invoking compile.bat first...
    call compile.bat
)
echo ================================================================
echo   Running Automated End-to-End System Tests
echo ================================================================
java -cp bin mccgrs.SystemTest
