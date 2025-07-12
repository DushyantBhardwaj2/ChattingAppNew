@echo off
echo ==========================================
echo 🔍 FIREBASE DATA STRUCTURE DIAGNOSTIC
echo ==========================================
echo.
echo This script will help you run comprehensive Firebase diagnostic
echo and capture the results directly in the terminal.
echo.
echo INSTRUCTIONS:
echo 1. Make sure your device/emulator is connected
echo 2. Launch the ChattingApp on your device
echo 3. This script will capture and display diagnostic logs
echo.
pause
echo.
echo Starting diagnostic log capture...
echo ==========================================
echo.

REM Clear previous logs and start capturing Firebase diagnostic logs
adb logcat -c
echo Cleared previous logs. Starting log capture...
echo.
echo 📱 Now launch the ChattingApp on your device/emulator
echo.
echo Waiting for diagnostic logs...
echo (Press Ctrl+C to stop when diagnostic is complete)
echo.

REM Capture only diagnostic logs with timestamps
adb logcat -s "FirebaseDiagnostic" -v time
