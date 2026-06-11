param(
    [string[]]$Versions = @(
        "1.19",
        "1.19.2",
        "1.19.3",
        "1.19.4",
        "1.20",
        "1.20.1",
        "1.20.2",
        "1.20.4",
        "1.20.5",
        "1.20.6",
        "1.21",
        "1.21.1",
        "1.21.3",
        "1.21.4"
    )
)

$root = Get-Location
$output = New-Item -ItemType Directory -Path "$root\build\output" -Force
$wrapper = "$root\gradlew.bat"

if (-not (Test-Path -LiteralPath $wrapper)) {
    Write-Host "gradlew.bat not found in $root" -ForegroundColor Red
    exit 1
}

$success = 0
$failed = @()

foreach ($ver in $Versions) {
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "Building WolfLang for Minecraft $ver..." -ForegroundColor Yellow
    Write-Host "========================================`n" -ForegroundColor Cyan

    & $wrapper build -PmcVersion=$ver --no-daemon --console=plain

    if ($LASTEXITCODE -eq 0) {
        $jar = "WolfLang-$ver.jar"
        $src = "$root\build\libs\$jar"
        if (Test-Path -LiteralPath $src) {
            Copy-Item -LiteralPath $src -Destination "$output\$jar" -Force
            Write-Host "  [$ver] OK -> $output\$jar" -ForegroundColor Green
            $success++
        } else {
            Write-Host "  [$ver] Built but jar not found at $src" -ForegroundColor Red
            $failed += $ver
        }
    } else {
        Write-Host "  [$ver] FAILED" -ForegroundColor Red
        $failed += $ver
    }

    Remove-Item -LiteralPath "$root\build" -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Build complete!" -ForegroundColor Cyan
Write-Host "Successful: $success" -ForegroundColor Green
if ($failed.Count -gt 0) {
    Write-Host "Failed: $($failed -join ', ')" -ForegroundColor Red
}
Write-Host "Output directory: $output" -ForegroundColor Cyan
