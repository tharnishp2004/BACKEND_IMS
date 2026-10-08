# run.ps1 - Smart backend starter: kills any process on port 8080, then starts Spring Boot

# Ensure the working directory is always this folder (inventory)
Set-Location $PSScriptRoot

Write-Host "Checking port 8080..." -ForegroundColor Cyan

$existing = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
if ($existing) {
    $pid8080 = $existing | Select-Object -ExpandProperty OwningProcess
    Stop-Process -Id $pid8080 -Force
    Write-Host "Killed old process on port 8080 (PID $pid8080)" -ForegroundColor Yellow
    Start-Sleep -Seconds 2
} else {
    Write-Host "Port 8080 is free." -ForegroundColor Green
}

Write-Host "Starting Spring Boot..." -ForegroundColor Cyan
& "$PSScriptRoot\mvnw.cmd" spring-boot:run

