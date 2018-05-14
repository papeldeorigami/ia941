**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Relatório da Aula 6 - SOAR: Controlando o WorldServer3D


## Atividade 1

O programa DemoJSOAR permite a execucao de regras do Soar, interage com o WorldServer3D e apresenta ferramentas de depuração, tais como botões step/mstep, uma visao em forma de árvore do Working Memory, um MindView, etc.

#### _O loop principal de simulação do DemoJSOAR também se encontra na classe Main. Explique seu funcionamento._

A cada iteração, o programa chama o método Step do SoarBridge, que faz o equivalente ao comando step do SoarDebugger, avançando uma fase de cada vez no ciclo de execução do SOAR.
Além disso, no loop, são exibidos no mind view os links de input e output.

#### _Acesse o código da classe SoarBridge.java, para compreender em mais detalhes o que está acontecendo. Investigue o funcionamento dos métodos step() e mstep() dessa classe._

Step e mstep servem para investigar o ciclo de execucao, sendo que o mstep vai uma fase de cada vez, em quanto o step roda um passo completo do Soar.

#### _Observe que essa classe já se utiliza das classes de apoio em WS3DProxy. Entenda e explique como é feito o acesso ao WorldServer3D, por meio do WS3DProxy.__

Um objeto da classe Environment, inicializado PELA Main, inicializa uma instancia do WS3DProxy e pega uma instancia para o World. O SoarBridge recebe uma referência ao environment e, à partir daí, iterage com o WorldServer3D, acessando as criaturas, enviando comandos, etc.

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
1. Comer, se estiver com combustível baixo
2. Pegar jóia, se estiver com combustível alto
3. Desviar de parede
4. Enxergar objetos
5. Andar na direção de jóias/comida
6. Procurar itens

* _Na versão a ser entregue na Atividade 2, o JSoarDebugger deve ser inibido, seguindo-se o procedimento acima._

## Atividade 2
 
Construir um controlador deliberativo, ao invés de reativo, para encontrar as jóias especificadas nos Leaflets da criatura, utilizando as técnicas de Planning aprendidas no tutorial do SOAR.

### Leaflets no SOAR

Para que o SOAR tenha acesso à informação dos Leaflets, estendemos a classe SoarBridge, metodo prepareInputLink, com a seguinte representação, onde cada folha com nome de cor representa o número de jóias necessário para completar aquele leaflet:
	CREATURE I4
		^LEAFLETS I8
			^LEAFLET I9
				^PAYMENT 50
				^RED 1
				^GREEN 3
				...
			^LEAFLET I10
				...

### Knapsack & desired result

Utilizamos o inicializador para criar um knapsack na criatura; desta forma, podemos contar quantas jóias de cada cor ela está carregando.

Sendo assim, o resultado esperado (^desired) ficou sendo a soma de cada cor dos três leaflets, como demonstrado no trecho abaixo do initialize-solution:

```
   (<desired>
               ^Red (+ <Red1> <Red2> <Red3>)
               ^Green (+ <Green1> <Green2> <Green3>)
               ^Blue (+ <Blue1> <Blue2> <Blue3>)
               ^Yellow (+ <Yellow1> <Yellow2> <Yellow3>)
               ^Magenta (+ <Magenta1> <Magenta2> <Magenta3>)
               ^White (+ <White1> <White2> <White3>))
```

### Dicas

* Para simplificar um pouco o código, criou-se uma regra de inicialização da criatura. Desta forma, pode-se inicializar contadores de jóias, eliminando a necessidade de rotinas duplicadas, tais como a see\*entity, que tinha duas versões (see\*entity\*with\*memory\*count e see\*entity\*without\*memory\*count).

* Foi introduzido ainda um operador de simulação (simulate-input) para permitir o desenvolvimento de código SOAR sem o Java. Isto é, simulou-se todos os inputs relevantes do WS3D, permitindo-se utilizar o SoarJavaDebugger diretamente, sem a necessidade de se iniciar o programa Java para verificar os resultados.

* Utilizou-se o VisualSoar para se organizar melhor o código, com um arquivo para cada operador, permitindo ainda a verificação de sintaxe no próprio editor. Para abrir o código no VisualSoar, basta acessar o arquivo "rules/solution.vsa".


### Detectar leaflet completo
```
sp {solution*evaluate*state*success
   (state <s> ^desired <d>
              ^name solution
              ^knapsack <k>
              ^traveledDistance <traveledDistance>)
   (<d> ^Red <Red>
        ^Green <Green>
        ^Blue <Blue>
        ^Yellow <Yellow>
        ^Magenta <Magenta>
        ^White <White>)        
   (<k> ^Red <Red> 
        ^Green <Green>
        ^Blue <Blue>
        ^Yellow <Yellow>
        ^Magenta <Magenta>
        ^White <White>)
-->
   (<s> ^success <d>)
   (write (crlf) | ****************************** |)
   (write (crlf) | ********** Success! ********** |)
   (write (crlf) | traveledDistance =  | <traveledDistance> ||)
   (write (crlf) | ****************************** |)
#   (halt)
}
```

### Entregar jóias no delivery spot

```
# Propose*move*delivery*spot:
sp {propose*move*delivery*spot
   (state <s> ^io.input-link <il>)
   (<il> ^CREATURE <creature>)
   (<creature> ^LEAFLETS.ALL_COMPLETED 1)
-->
   (<s> ^operator <o> +)
   (<o> ^name moveDeliverySpot)
}
   
# move to delivery spot position (defaults to 0,0)
sp {apply*move*delivery*spot
   (state <s> ^operator <o>
              ^io <io>)
   (<io> ^input-link <il>)           
   (<io> ^output-link <ol>)
   (<o> ^name moveDeliverySpot)
-->
   (<ol> ^MOVE <command>)
   (<command> ^Vel 1)
   (<command> ^VelR 1)
   (<command> ^VelL 1)
   (<command> ^X 0)
   (<command> ^Y 0)
}

# Apply*moveDeliverySpot*remove-move:
# If the moveDeliverySpot operator is selected,
# and there is a completed move command on the output link,
# then remove that command.   
sp {apply*moveDeliverySpot*remove-move
(state <s> ^operator.name moveDeliverySpot
           ^io.output-link <out>)
(<out> ^MOVE <move>)
(<move> ^status complete)
-->
(<out> ^MOVE <move> -)}   

# Move Jewel vs Move Delivery Spot Preferences - Move DeliverySpot wins
sp {moveJewel*moveDeliverySpot*preferences
(state <s> ^attribute operator
           ^impasse tie
           ^item <o> {<> <o> <o2>}
           ^superstate <ss>)
(<ss> ^io.input-link <il>)
(<o> ^name moveJewel)
(<o2> ^name moveDeliverySpot)
-->
(<ss> ^operator <o2> > <o>)} 

# Move Food vs Move Delivery Spot Preferences - Move DeliverySpot wins
sp {moveFood*moveDeliverySpot*preferences
(state <s> ^attribute operator
           ^impasse tie
           ^item <o> {<> <o> <o2>}
           ^superstate <ss>)
(<ss> ^io.input-link <il>)
(<o> ^name moveFood)
(<o2> ^name moveDeliverySpot)
-->
(<ss> ^operator <o2> > <o>)} 

```
## Conclusão

O programa desenvolvido utiliza raciocinio deliberativo para atingir o objetivo de trocar leaflets por pontos.
Uma melhoria possível seria priorizar jóias dos leaflets na busca, considerando ainda a pontuação de cada leaflet.


Foram definidos os seguintes operadores principais, com suas respectivas elaborações e sub-estados:

- plan (Planejar)
    - se algum leaflet estiver completo, mover para o delivery spot
    - propor comando move para todas as joias em memoria que ajudem a completar os leaflets
    - utilizar o selection problem (que vem na pasta default do soar) para determinar o melhor movimento
    - o melhor movimento é o primeiro movimento que irá levar ao menor caminho para completar todos os leaflets
    - priorizar leaflets de maior valor
    - se nenhuma jóia faltante estiver presente, e nenhum leaflet não estiver completo, mudar para o estado rotate360

- rotate360: girar 360 graus no inicio para certificar-se de ter visto todas as joias

- deliver (Trocar leaflets)
    - se estiver na posição do delivery spot (0,0), trocar todos os leaflets completos
    - se não tiver mais nenhum leaflet para trocar, mas tiver algum incompleto ainda, voltar para rotate360

- move (mover em direcao a um objeto, target)
    - a posicao pode ser uma joia, uma comida ou o delivery spot
    - o objeto target é determinado no planejamento
    - reativamente desviar ou esconder obstaculos pelo caminho
    - detectar se chegou perto do objeto, o suficiente para pegar (se for joia) ou comer (se for comida)
    - colocar comando move no output, interromper execucao para que o java processe o mesmo, e depois remover
    - voltar para o estado de planejamento se enxergar uma joia nova, que seja necessaria para algum leaflet

Operadores propostos em qualquer estado:
- see: salvar em memoria qualquer novo objeto visto
- get: sempre que encontrar alguma joia próxima, que sirva para completar algum leaflet, pegar
- hide: sempre que encontrar alguma jóia próxima, que não seja necessária para algum leaflet, esconder
- eat: sempre que encontrar comida proxima, comer
- wait: proposto sempre que parar em um loop de state no-change, onde nenhum outro operador tenha sido proposto (geralmente é algum bug, alguma situação imprevista)


Problema: o selection problem sai do impasse com um movimento de menor distancia, mas não leva em consideração os próximos movimentos
Causa:
- o impasse só pode ser resolvido com os operadores propostos
- cada operador contém apenas um destino (uma joia)
Possiveis solucoes:
1. propor operadores com todas as sequencias possiveis para pegar as joias faltantes; não parece haver suporte no SOAR para manipulação desse tipo de lista
2. criar um operador para cada joia, gerar impasse para cada proxima joia, salvar o numeric-value como resultado do calculo até a última jóia. A dificuldade aqui é gerar os impasses em cascata, isto é, para cada estado, gerar um subestado de impasse sem sair do primeiro impasse, ainda não sei fazer isso.
