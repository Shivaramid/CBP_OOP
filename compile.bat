@echo off
echo ================================================================
echo   Compiling Municipal Civic Complaint System (MCC^&GRS)
echo ================================================================

if not exist bin mkdir bin

javac -d bin -cp bin src\mccgrs\enums\*.java src\mccgrs\exception\*.java src\mccgrs\util\*.java src\mccgrs\model\*.java src\mccgrs\repository\*.java src\mccgrs\service\*.java src\mccgrs\scheduler\*.java src\mccgrs\ui\*.java src\mccgrs\Main.java test\mccgrs\SystemTest.java

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Compilation finished with 0 errors.
) else (
    echo [ERROR] Compilation failed.
)
