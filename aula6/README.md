**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Relatório da Aula 6 - SOAR: Controlando o WorldServer3D


## Atividade 1

O programa DemoJSOAR permite a execucao de regras do Soar, interage com o WorldServer3D e apresenta ferramentas de depuração, tais como botões step/mstep, uma visao em forma de árvore do Working Memory, um MindView, etc.

#### _Observe o uso da classe NativeUtils para fazer o dowload do arquivo soar-rules.soar a partir do próprio arquivo JAR do DemoJSOAR_

Arquivo carregado com o caminho '/soar-rules.soar', o que indica que ele está na pasta raiz do JAR. Depois disso, as regras são passadas ao SoarBridge, que se encarrega da interface com o Soar.

#### _O loop principal de simulação do DemoJSOAR também se encontra na classe Main. Explique seu funcionamento._

A cada iteração, o programa chama o método Step do SoarBridge, que faz o equivalente ao comando step do SoarDebugger, avançando uma fase de cada vez no ciclo de execução do SOAR.
Além disso, no loop, são exibidos no mind view os links de input e output.

#### _Acesse o código da classe SoarBridge.java, para compreender em mais detalhes o que está acontecendo. Investigue o funcionamento dos métodos step() e mstep() dessa classe. Observe que essa classe já se utiliza das classes de apoio em WS3DProxy. Entenda e explique como é feito o acesso ao WorldServer3D, por meio do WS3DProxy._

* step e mstep servem para investigar o ciclo de execucao, sendo que o mstep vai uma fase de cada vez, em quanto o step roda um passo completo do Soar.

* acesso ao worldserver3d com ws3dproxy: um objeto da classe Environment, inicializado PELA Main, inicializa uma instancia do WS3DProxy e pega uma instancia para o World. O SoarBridge recebe uma referência ao environment e, à partir daí, iterage com o WorldServer3D, acessando as criaturas, enviando comandos, etc.


#### _Explique como é feita a leitura do estado do ambiente no WorldServer3D, e como esses dados sensoriais são enviado para o SOAR. Da mesma forma, explique como os dados enviados pelo SOAR são aproveitados para controlar a criatura no WorldServer3D. Registre suas conclusões no relatório de atividades._

* O metodo prepareInputLink é responsável por criar os elementos de memória de trabalho (WMEs) relacionados às criaturas, às coisas e aos seus atributos, refletindo o estado do WorldServer3D.

* O processOutputLink processa os comandos de saída do SOAR, controlando o WorldServer3D de acordo.

#### _Acesse o conteúdo do arquivo de regras SOAR: soar-rules.soar e tente entender seu funcionamento. Explique o princípio lógico de seu funcionamento._

Os comportamentos definidos pelas regras podem ser resumidos em:
* __Procurar itens (wander)__: girar em torno de si com VelR=2 (para a esquerda)
* __Enxergar itens (seeEntityWithMemoryCount/seeEntityWithoutMemoryCount)__: atualiza memoria com itens no campo de visao (JEWEL/FOOD)
* __Mover-se para/pegar uma jóia__ (moveJewel/getJewel): caminha na direção da jóia (JEWEL) e pega quando está próximo o suficiente (30)
* __Mover-se para/comer comida__ (moveFood/eatFood): caminha na direção da comida e a consome quando está próxima o suficiente (30) 
* __Evitar paredes (avoidBrick)__: quando se aproxima da parede (61), gira a uma fracao da distância para evitar o obstaculo (55/distancia).

A aplicação das regras associadas a esses comportamentos se dá na ordem definida pelos operadores de __preferência__, no final do arquivo. Podemos resumir as preferências da seguinte forma, em ordem decrescente:
* Comer, se estiver com combustível baixo
* Pegar jóia, se estiver com combustível alto
* Desviar de parede
* Enxergar objetos
* Andar na direção de jóias/comida
* Procurar itens

* _Na versão a ser entregue na Atividade 2, o JSoarDebugger deve ser inibido, seguindo-se o procedimento acima._

## Atividade 2
 
Construir um controlador deliberativo


- Estrategias de inicio: todas as joias ja estao lá; rodar em torno de si ate aparecerem todas as joias; etc.
- pegar joia vermelha que apareceu mais perto do que aquela que estava no plano original


So pequenas modificacoes no programa em java
Trazer informacoes do leaflet (so traz outras infos do mundo)

Leaflets: 3 combinacoes com 3 joias; 9 joias ao todo. Se joias com mesma cor, decidir pela mais proxima ou uma sequencia mais proxima

gerar planejamento para raciocinio deliberativo
executar primeiro operador, primeira tarefa
trocar no leaflet spot em pontos
checar se atingiu o objetivo
indicar que atingiu a tarefa, exemplo parar e ficar girando
