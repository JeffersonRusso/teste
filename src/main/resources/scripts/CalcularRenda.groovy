package scripts

/**
 * CalcularRenda.groovy: Refatorado para o padrão de Função Pura.
 * Recebe: historico_pagamentos (Map)
 * Retorna: Map com a renda calculada.
 */

def historico = input("historico_pagamentos")
def pagamentos = historico?.pagamentos

if (!pagamentos) {
    return [renda_calculada: 0.0, motivo: "sem_historico"]
}

double total = pagamentos.sum { it.valor ?: 0.0 }
double media = total / pagamentos.size()

// Lógica de negócio: Renda é 3.5x a média de pagamentos
return [
    renda_calculada: media * 3.5,
    stats: [
        media: media,
        total_analisado: pagamentos.size()
    ]
]
