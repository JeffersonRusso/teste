package scripts

/**
 * AnalisarFraudeDevice.groovy: Refatorado para o padrão de Função Pura.
 * Recebe: device_reputation, geo_info
 * Retorna: Map com o sinal de fraude.
 */

def reputation = input("device_reputation")
def geo = input("geo_info")

boolean isEmulator = reputation?.is_emulator ?: false
boolean isRooted = reputation?.is_rooted ?: false
String country = geo?.country ?: "BR"

String risco = "BAIXO"
String motivo = "OK"

if (isEmulator && isRooted) {
    risco = "ALTISSIMO"
    motivo = "Emulador com Root detectado"
} else if (country != "BR") {
    risco = "MEDIO"
    motivo = "Acesso fora do país: ${country}"
} else if (isEmulator) {
    risco = "MEDIO"
    motivo = "Uso de emulador"
}

return [
    sinal_fraude_device: [
        nivel: risco,
        motivo: motivo,
        analise: [
            is_emulator: isEmulator,
            country: country
        ]
    ]
]
