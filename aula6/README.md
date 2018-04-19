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

Para que o SOAR tenha acesso à informação dos Leaflets, estendemos a classe SoarBridge, metodo prepareInputLink, com a seguinte representação:
	CREATURE I4
		^LEAFLETS I8
			^LEAFLET I9
				^ITEM I11
					^COLOR RED
					^TARGET 1
					^COLLECTED 0
				^ITEM I12
					^COLOR GREEN
					^TARGET 0
					^COLLECTED 0
				...
			^LEAFLET I10
				...

### Inicializador da criatura

Para simplificar um pouco o código, criamos uma regra de inicialização da criatura. Desta forma, podemos inicializar contadores de jóias, eliminando a necessidade de rotinas duplicadas, tais como a see\*entity, que tinha duas versões (see\*entity\*with\*memory\*count e see\*entity\*without\*memory\*count).

### Knapsack

Utilizamos o inicializador para criar um knapsack na criatura; desta forma, podemos contar quantas jóias de cada cor ela está carregando.

### Salvar quantidade de joias por leaflet

A regra getJewel foi estendida:

```
# This will update the leaflets jewels collected count
sp {apply*get*jewel*update*leaflets*collected
   (state <s> ^operator <o>
              ^io <io>)
   (<io> ^input-link <il>)      
   (<o> ^name getJewel)
   (<il> ^CREATURE <creature>) 
   (<creature> ^MEMORY <memory>)
   (<memory> ^COUNT <quantity>)  
   (<memory> ^ENTITY <memoryEntity>)

   # additional checks to enable leaflets
   (<memoryEntity> ^COLOR <color>)
   (<creature> ^LEAFLETS.LEAFLET <leaflet>)
   (<leaflet> ^COLLECTED_JEWELS <collectedJewels>)
   (<leaflet> ^JEWEL <leafletJewel>)
   (<leafletJewel> ^COLOR <color>)
   (<leafletJewel> ^NEEDED <needed>)
   (<leafletJewel> ^COLLECTED <collected> < <needed>)
-->
   (<leafletJewel> ^COLLECTED <collected> - (+ <collected> 1))
   (<leaflet> ^COLLECTED_JEWELS <collectedJewels> - (+ <collectedJewels> 1))
}

```

### Detectar leaflet completo

Assumindo que todo leaflet tem 9 jóias, a detecção de leaflets completos ficou da seguinte forma:

```
##############################  LEAFLETS DETECTION #####################################
# detect leaflets completion

# detect if a single leaflet is complete
sp {detect*leaflet*complete
   (state <s> ^io.input-link <il>)
   (<il> ^CREATURE <creature>) 
   (<creature> ^LEAFLETS.LEAFLET <leaflet>)
   -(<leaflet> ^COMPLETED)
   (<leaflet> ^COLLECTED_JEWELS 9)
-->
   (<leaflet> ^COMPLETED 1)
}

# detect if all leaflets are complete
sp {detect*all*leaflets*complete
   (state <s> ^io.input-link <il>)
   (<il> ^CREATURE <creature>) 
   (<creature> ^LEAFLETS <leaflets>)
   (<leaflets> ^LEAFLET <leaflet1>)
   (<leaflet1> ^COMPLETED 1)
   (<leaflets> ^LEAFLET <leaflet2>)
   (<leaflet2> ^COMPLETED 1)
   (<leaflets> ^LEAFLET <leaflet3>)
   (<leaflet3> ^COMPLETED 1)
-->
   (<leaflets> ^ALL_COMPLETED 1)
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
