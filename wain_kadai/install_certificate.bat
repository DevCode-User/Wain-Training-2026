@echo off
REM 自己署名証明書をWindowsの信頼できるストアに登録するスクリプト
REM 管理者権限で実行が必要です

setlocal enabledelayedexpansion

cd /d %~dp0

REM Javaホームを検出
if defined JAVA_HOME (
    set KEYTOOL=%JAVA_HOME%\bin\keytool.exe
) else (
    REM デフォルトパスを試す
    set KEYTOOL=C:\java\jdk-23\bin\keytool.exe
)

if not exist "!KEYTOOL!" (
    echo エラー: keytoolが見つかりません
    echo JAVA_HOMEを設定してください
    pause
    exit /b 1
)

echo.
echo ========================================
echo  証明書をWindowsストアに登録します
echo ========================================
echo.

REM keytoolで証明書をエクスポート
echo 1. 証明書をエクスポート中...
"!KEYTOOL!" -export -alias server -keystore wain_kadai\keystore.jks -storepass password -file wain_kadai\server.crt -noprompt

if errorlevel 1 (
    echo エラー: 証明書のエクスポートに失敗しました
    pause
    exit /b 1
)

echo ✓ 証明書をエクスポートしました

echo.
echo 2. 証明書をWindowsストアに登録中...
echo    (管理者権限が必要です)

REM PowerShellで証明書をシステムストアに追加
powershell -Command "Import-Certificate -FilePath wain_kadai\server.crt -CertStoreLocation Cert:\LocalMachine\Root" -ErrorAction Continue >nul 2>&1

if errorlevel 1 (
    echo.
    echo ※ 管理者権限での実行が必要です
    echo    このバッチファイルを管理者権限で実行してください
    echo.
    pause
    exit /b 1
)

echo ✓ 証明書をWindowsストアに登録しました

echo.
echo ========================================
echo  登録完了！
echo ========================================
echo.
echo ブラウザを再度開いて以下にアクセスしてください:
echo https://localhost:8443/admin/login.html
echo.
pause
