#!/bin/bash

JMETER_HOME="src/main/resources/apache-jmeter-5.6.3"
TEST_PLAN="teste_carga_500_threads.jmx"
RESULT_LOG="resultados.jtl"
REPORT_DIR="report-dashboard"

# Detecta o sistema operacional
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OSTYPE" == "win32" ]]; then
    echo "Ambiente Windows detectado. Usando jmeter.bat..."
    JMETER_BIN="$JMETER_HOME/bin/jmeter.bat"
else
    echo "Ambiente Unix/Linux detectado. Usando jmeter..."
    JMETER_BIN="$JMETER_HOME/bin/jmeter"
    chmod +x "$JMETER_BIN"
fi

echo "Iniciando teste de carga massiva (1000 threads)..."

# Limpa resultados anteriores
rm -f "$RESULT_LOG"
rm -rf "$REPORT_DIR"

# Executa o teste em modo non-GUI
"$JMETER_BIN" -n -t "$TEST_PLAN" -l "$RESULT_LOG" -e -o "$REPORT_DIR"

echo ""
echo "Teste finalizado!"
if [ -f "$RESULT_LOG" ]; then
    echo "Resultados salvos em: $RESULT_LOG"
    echo "Relatorio HTML gerado em: $REPORT_DIR/index.html"
else
    echo "ERRO: O arquivo de log não foi gerado. Verifique se o JMeter rodou corretamente."
fi
