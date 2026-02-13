package scripts

import com.fasterxml.jackson.databind.JsonNode

// Inputs: score_comportamental, serasa_info, analise_cliente_result
def score_comportamental = input("score_comportamental")
def serasa_info = input("serasa_info")
def analise_cliente_result = input("analise_cliente_result")

// Helper para extrair int de Map ou JsonNode
int getInt(Object obj, String key) {
    if (obj == null) return 0
    if (obj instanceof Map) return obj.get(key) as Integer ?: 0
    if (obj instanceof JsonNode) return obj.get(key)?.asInt() ?: 0
    return 0
}

// Helper para extrair String de Map ou JsonNode
String getString(Object obj, String key, String defaultValue) {
    if (obj == null) return defaultValue
    if (obj instanceof Map) return obj.get(key) ?: defaultValue
    if (obj instanceof JsonNode) return obj.get(key)?.asText() ?: defaultValue
    return defaultValue
}

int scoreComp = getInt(score_comportamental, "score_comp")
int scoreSerasa = getInt(serasa_info, "score")
String recomendacao = getString(analise_cliente_result, "recommendation", "NEUTRAL")

double pesoComp = 0.6
double pesoSerasa = 0.4

if (recomendacao == "UPSELL") {
    pesoComp += 0.1
    addMetadata("bonus_upsell_aplicado", true)
}

int scoreFinal = (int) ((scoreComp * pesoComp) + (scoreSerasa * pesoSerasa))

// Insights de Negócio
addMetadata("score_final_calculado", scoreFinal)
addMetadata("peso_comportamental", pesoComp)
addMetadata("peso_serasa", pesoSerasa)
addMetadata("input_score_comp", scoreComp)
addMetadata("input_score_serasa", scoreSerasa)

return [score_consolidado: scoreFinal]
