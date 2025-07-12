# Firebase Data Structure Diagnostic Script
# PowerShell version

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "🔍 FIREBASE DATA STRUCTURE DIAGNOSTIC" -ForegroundColor Yellow
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "This script will run comprehensive Firebase diagnostic" -ForegroundColor White
Write-Host "and capture the results directly in PowerShell." -ForegroundColor White
Write-Host ""

Write-Host "INSTRUCTIONS:" -ForegroundColor Green
Write-Host "1. Make sure your Android device/emulator is connected" -ForegroundColor White
Write-Host "2. Launch the ChattingApp on your device" -ForegroundColor White  
Write-Host "3. This script will capture and display diagnostic logs" -ForegroundColor White
Write-Host ""

Read-Host "Press Enter when ready to start diagnostic capture"

Write-Host ""
Write-Host "Starting diagnostic log capture..." -ForegroundColor Yellow
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

# Clear previous logs
Write-Host "Clearing previous logs..." -ForegroundColor Gray
adb logcat -c

Write-Host "📱 Now launch the ChattingApp on your device/emulator" -ForegroundColor Green
Write-Host ""
Write-Host "Waiting for diagnostic logs..." -ForegroundColor Yellow
Write-Host "(Press Ctrl+C to stop when diagnostic is complete)" -ForegroundColor Gray
Write-Host ""

# Capture diagnostic logs with color coding
try {
    adb logcat -s "FirebaseDiagnostic" -v time | ForEach-Object {
        $line = $_
        if ($line -match "❌|🚨|CRITICAL|ERROR") {
            Write-Host $line -ForegroundColor Red
        }
        elseif ($line -match "⚠️|WARNING") {
            Write-Host $line -ForegroundColor Yellow
        }
        elseif ($line -match "✅|SUCCESS") {
            Write-Host $line -ForegroundColor Green
        }
        elseif ($line -match "🔍|📊|💬|👥|🎯") {
            Write-Host $line -ForegroundColor Cyan
        }
        else {
            Write-Host $line -ForegroundColor White
        }
    }
}
catch {
    Write-Host "Error capturing logs: $_" -ForegroundColor Red
    Write-Host "Make sure ADB is in your PATH and device is connected" -ForegroundColor Yellow
}
