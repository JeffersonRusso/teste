package scripts

import com.fasterxml.jackson.databind.JsonNode

// Inputs injetados via 'requires'
def device_reputation = input("device_reputation")
def geo_info = input("geo_info")

boolean isEmulator = false
boolean isRooted = false
String country = "BR"

// Helper para extrair boolean de Map ou JsonNode
boolean getBoolean(Object obj, String key) {
    if (obj == null) return false
    if (obj instanceof Map) return obj.get(key) ?: false
    if (obj instanceof JsonNode) return obj.get(key)?.asBoolean() ?: false
    return false
}

// Helper para extrair String de Map ou JsonNode
String getString(Object obj, String key, String defaultValue) {
    if (obj == null) return defaultValue
    if (obj instanceof Map) return obj.get(key) ?: defaultValue
    if (obj instanceof JsonNode) return obj.get(key)?.asText() ?: defaultValue
    return defaultValue
}

isEmulator = getBoolean(device_reputation, "is_emulator")
isRooted = getBoolean(device_reputation, "is_rooted")
country = getString(geo_info, "country", "BR")

String risco = "BAIXO"
String motivo = "OK"

if (isEmulator && isRooted) {
    risco = "ALTISSIMO"
    motivo = "Emulador com Root detectado"
} else if (country != "BR") {
    risco = "MEDIO"
    motivo = "Acesso fora do país: " + country
} else if (isEmulator) {
    risco = "MEDIO"
    motivo = "Uso de emulador"
}

// Insights de Negócio
addMetadata("device_risco_calculado", risco)
addMetadata("device_motivo_principal", motivo)
addMetadata("device_is_emulator", isEmulator)
addMetadata("device_country", country)

return [
    sinal_fraude_device: [
        nivel: risco,
        motivo: motivo,
        timestamp: System.currentTimeMillis()
    ]
]
