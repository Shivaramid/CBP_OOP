@echo off
if not exist bin\mccgrs\Main.class (
    echo [INFO] Binaries not found. Invoking compile.bat first...
    call compile.bat
)
echo ================================================================
echo   Launching Municipal Civic Complaint ^& Grievance System
echo ================================================================
java -cp bin mccgrs.Main
