#!/bin/bash

# Script para executar a aplicação JavaFX
echo "Executando Sistema de Gerenciamento - Petshop (JavaFX)"

# Executar com o plugin JavaFX
mvn clean compile javafx:run

# Caso o plugin falhe, usar exec:java como alternativa
if [ $? -ne 0 ]; then
    echo "Tentando executar com exec:java..."
    mvn exec:java -Dexec.mainClass="app.Main"
fi