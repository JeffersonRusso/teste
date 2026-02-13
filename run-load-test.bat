@echo off
echo Iniciando teste de carga com JMeter...

set JMETER_BIN=src\main\resources\apache-jmeter-5.6.3\bin\jmeter.bat
set TEST_PLAN=teste-carga-orquestrador.jmx
set RESULT_LOG=resultados.jtl
set REPORT_DIR=report-dashboard

if exist %RESULT_LOG% del %RESULT_LOG%
if exist %REPORT_DIR% rmdir /s /q %REPORT_DIR%

call "%JMETER_BIN%" -n -t "%TEST_PLAN%" -l "%RESULT_LOG%" -e -o "%REPORT_DIR%"

echo.
echo Teste finalizado!
echo Resultados salvos em: %RESULT_LOG%
echo Relatorio HTML gerado em: %REPORT_DIR%\index.html
pause