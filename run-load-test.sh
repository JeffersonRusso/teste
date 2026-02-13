#!/bin/bash

JMETER_HOME="src/main/resources/apache-jmeter-5.6.3"
TEST_PLAN="teste-carga-orquestrador.jmx"
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

echo "Iniciando teste de carga com JMeter..."

# Limpa resultados anteriores
rm -f "$RESULT_LOG"
rm -rf "$REPORT_DIR"

# Executa o teste
# No Windows, o .bat cuida do HEAP, mas podemos tentar passar via env var se necessário.
# O comando 'cmd /c' pode ser necessário para rodar .bat do bash corretamente em alguns casos,
# mas geralmente chamar direto funciona se o path estiver correto.

"$JMETER_BIN" -n -t "$TEST_PLAN" -l "$RESULT_LOG" -e -o "$REPORT_DIR"

echo ""
echo "Teste finalizado!"
if [ -f "$RESULT_LOG" ]; then
    echo "Resultados salvos em: $RESULT_LOG"
    echo "Relatorio HTML gerado em: $REPORT_DIR/index.html"
else
    echo "ERRO: O arquivo de log não foi gerado. Verifique se o JMeter rodou corretamente."
fi