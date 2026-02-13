package scripts

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode

// Input: historico_pagamentos (Pode ser Map ou JsonNode/ObjectNode)
def historico_pagamentos = input("historico_pagamentos")

def pagamentos = null

if (historico_pagamentos instanceof Map) {
    pagamentos = historico_pagamentos.get("pagamentos")
} else if (historico_pagamentos instanceof JsonNode) {
    // Se for JsonNode, usamos o método get() do Jackson
    def node = historico_pagamentos.get("pagamentos")
    if (node != null && node.isArray()) {
        // Converte ArrayNode para List para facilitar iteração
        pagamentos = []
        node.forEach { item -> pagamentos.add(item) }
    }
}

if (!pagamentos) {
    addMetadata("motivo_renda_zero", "sem_historico_pagamentos")
    return [renda_calculada: 0.0]
}

double total = 0.0
for (Object p : pagamentos) {
    double valor = 0.0
    if (p instanceof Map) {
        valor = (p.get("valor") as Number).doubleValue()
    } else if (p instanceof JsonNode) {
        valor = p.get("valor").asDouble()
    }
    total += valor
}

double media = total / pagamentos.size()

// Lógica complexa de negócio: Renda é 3.5x a média de pagamentos
double renda = media * 3.5

// Insights de Negócio
addMetadata("media_pagamentos", media)
addMetadata("fator_multiplicador", 3.5)
addMetadata("total_pagamentos_analisados", pagamentos.size())

return [renda_calculada: renda]
