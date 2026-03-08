package main

import (
	"fmt"
	"net/http"
	"time"
)

/**
 * Mock Server de Alta Performance em Go.
 * Substitui o WireMock para testes de volumetria massiva.
 * Simula um delay fixo de 100ms por chamada.
 */
func main() {
	delay := 100 * time.Millisecond

	// Middleware para log simples
	logRequest := func(h http.HandlerFunc) http.HandlerFunc {
		return func(w http.ResponseWriter, r *http.Request) {
			h(w, r)
		}
	}

	// v1/auth
	http.HandleFunc("/v1/auth", logRequest(func(w http.ResponseWriter, r *http.Request) {
		time.Sleep(delay)
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"access_token": "secret-token-123"}`)
	}))

	// v1/cliente/{doc}
	http.HandleFunc("/v1/cliente/", logRequest(func(w http.ResponseWriter, r *http.Request) {
		time.Sleep(delay)
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"id":"123456789","nome":"João da Silva","cpf":"12345678900"}`)
	}))

	// v1/conta/{cid}
	http.HandleFunc("/v1/conta/", logRequest(func(w http.ResponseWriter, r *http.Request) {
		time.Sleep(delay)
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"id_conta":"987654321","status":"ATIVA"}`)
	}))

	// v1/score/{cpf}
	http.HandleFunc("/v1/score/", logRequest(func(w http.ResponseWriter, r *http.Request) {
		time.Sleep(delay)
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"score_atual":850}`)
	}))

	// v1/hotlist/{cpf}
	http.HandleFunc("/v1/hotlist/", logRequest(func(w http.ResponseWriter, r *http.Request) {
		time.Sleep(delay)
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"status":"LIBERADO"}`)
	}))

	// v1/investimentos/{aid}
	http.HandleFunc("/v1/investimentos/", logRequest(func(w http.ResponseWriter, r *http.Request) {
		time.Sleep(delay)
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"patrimonio_total":150000.00}`)
	}))

	// v1/cartoes/{aid}
	http.HandleFunc("/v1/cartoes/", logRequest(func(w http.ResponseWriter, r *http.Request) {
		time.Sleep(delay)
		w.Header().Set("Content-Type", "application/json")
		fmt.Fprint(w, `{"id_cartao":"5555-4444-3333-2222"}`)
	}))

	fmt.Println("🚀 Mock Server Go rodando em http://localhost:9999")
	fmt.Println("⏱️ Delay configurado: 100ms")

	server := &http.Server{
		Addr:         ":9999",
		ReadTimeout:  5 * time.Second,
		WriteTimeout: 10 * time.Second,
		IdleTimeout:  120 * time.Second,
	}

	if err := server.ListenAndServe(); err != nil {
		panic(err)
	}
}
