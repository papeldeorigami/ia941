**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Aula 13 - LIDA controlando o WorldServer3D

O codigo-fonte das atividades desenvolvidas pelo aluno encontra-se disponivel em:
https://github.com/papeldeorigami/ia941

## Atividade 1

O DemoLida foi baixado através do link indicado na página da disciplina. Seu código foi estudado.

## Atividade 2

A janela do Lida foi habilitada com a propriedade lida.gui.enable=true.

## Atividade 3

O problema de se levar o agente a um determinado ponto, desviando de obstáculos, foi dividido nas seguintes etapas:

1 - Modelar o mundo como um grafo, com destino e obstaculos imaginarios
    
    - Importamos a biblioteca [https://github.com/xaguzman/pathfinding](pathfinding)
    - Definimos um grid de 80 por 60 (o tamanho default do mundo dividido por dez)
    - Ocupamos alguns pontos do grid com obstaculos "imaginários", i.e. setamos com o valor 1, sem ter nenhum objeto criado no mundo ainda
    - Apenas para auxiliar o desenvolvimento, determinamos um caminho fixo com um destino fixo
    - O caminho é representado por um vetor chamado "path"

2 - Estabelecer um mecanismo para planejar e estimular a o agente a andar em direcao a um objetivo

    - Salvar o grid e o plano no sensory memory
    - Fazer o agente detectar o "próximo passo": o primeiro elemento do vetor path que está na sensory memory

3 - Implementar Acao Replanejar

    - Detectar objetos e estimular o agente a replanejar o destino

4 - preencher com objetos do mundo

    - Fazer link com o WS3D para: detectar clique do mouse e detectar obstaculos
