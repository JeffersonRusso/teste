package scripts

/**
 * ConsolidarScore.groovy: Refatorado para o padrão de Função Pura.
 * Recebe: score_comportamental, serasa_info, analise_cliente_result
 * Retorna: Map com o score consolidado.
 */

def scoreComp = input("score_comportamental")?.score_comp ?: 0
def scoreSerasa = input("serasa_info")?.score ?: 0
def recomendacao = input("analise_cliente_result")?.recommendation ?: "NEUTRAL"

double pesoComp = 0.6
double pesoSerasa = 0.4

if (recomendacao == "UPSELL") {
    pesoComp += 0.1
}

int scoreFinal = (int) ((scoreComp * pesoComp) + (scoreSerasa * pesoSerasa))

return [
    score_consolidado: scoreFinal,
    detalhes: [
        peso_comp: pesoComp,
        peso_serasa: pesoSerasa,
        recomendacao_base: recomendacao
    ]
]
