**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Relatório da Aula 3 - Tutorial 2 do SOAR

As atividades abaixo foram realizadas com a versão 9.6 do tutorial, disponível em:
https://soar.eecs.umich.edu/articles/downloads/soar-suite/228-soar-tutorial-9-6-0

## Atividade 1

_Como o estado atual da criatura é considerado nas regras?_

Regra de proposição examina o conteúdo das células adjacentes em busca de comida.
Regra de aplicação move na direção da comida.
Regra de remoção para evitar acúmulo de movimentos anteriores na WME move.

_Como a decisão de ação escolhida é aplicada de fato ao jogo?_

A regra de proposição busca comida no elemento de memória "content"
de todas as direções possíveis (north, south, east, west) do input-link:
   (state <s> ^io.input-link.my-location.<dir>.content 
Então o movimento é escrito em io.output-link.move:
   (<ol> ^move.direction <dir>)}

_Como esse conjunto de regras escolhe a direção para a qual a criatura deve se mover?_

Basta que o conteudo tenha comida normal ou bonus.

_Para que serve a regra apply*move-to-food*remove-move?_

Para evitar que os movimentos anteriores se acumulem no elemento move.

_O que aconteceria se ela não existisse?_

Todos os movimentos ficam acumulados no WME move:

```
print I3
(I3 ^move M95 ^move M94 ^move M93 ^move M92 ^move M91 ^move M90 ^move M89
 ^move M88 ^move M87 ^move M86 ^move M85 ^move M84 ^move M83 ^move M82
 ^move M81 ^move M80 ^move M79 ^move M78 ^move M77 ^move M76 ^move M75
 ^move M74 ^move M73 ^move M72 ^move M71 ^move M70 ^move M69 ^move M68
 ^move M67 ^move M66 ^move M65 ^move M64 ^move M63 ^move M62 ^move M61
 ^move M60 ^move M59 ^move M58 ^move M57 ^move M56 ^move M55 ^move M54
 ^move M53 ^move M52 ^move M51 ^move M50 ^move M49 ^move M48 ^move M47
 ^move M46 ^move M45 ^move M44 ^move M43 ^move M42 ^move M41 ^move M40
 ^move M39 ^move M38 ^move M37 ^move M36 ^move M35 ^move M34 ^move M33
 ^move M32 ^move M31 ^move M30 ^move M29 ^move M28 ^move M27 ^move M26
 ^move M25 ^move M24 ^move M23 ^move M22 ^move M21 ^move M20 ^move M19
 ^move M18 ^move M17 ^move M16 ^move M15 ^move M14 ^move M13 ^move M12
 ^move M11 ^move M10 ^move M9 ^move M8 ^move M7 ^move M6 ^move M5
 ^move M4 ^move M3 ^move M2)
```

_Quais são as limitações desse programa?_

Ele não consegue avançar se não encontrar mais comida.

_O que seria necessário fazer para que ele não ficasse paralizado, depois de um tempo?_

Mudar a proposição para não depender da existência de comida.

## Atividade 2

Seguindo o tutorial, foi implementado o movimentador para o norte e depois ajustado para buscar comida.
Durante esse desenvolvimento, foi importante aprender a utilizar o debugger.

* Uso da interface de entrada e saída de um programa Soar

	^io.input-link: conteudo das celulas adjacentes
	^io.output-link: determina a direcao do movimento

* Uso de shortcuts em programas Soar
	
	Ao inves de `(<s> ^io <io>) (<io>.input-link)` pode-se utilizar `(<s> ^io.input-link)`

* Uso do SoarJavaDebugger para acompanhar o processo de escolha e aplicação de operadores, por meio de traces.

	Rodar com step e utilizar e mover a barra verde para escolher em qual etapa o programa deve parar.
	Tambem é util usar `run 1 -p` para ir etapa por etapa.
	Existe ainda a possibilidade de se utilizar comandos write para simplificar o debug.
	
* Diferença entre ações o-supported e i-supported, e WMEs persistentes

	Na fase de proposição, em geral são i-supported; na fase apply, são o-supported. As o-supported são WME persistentes.

* Uso de preferências entre operadores

	Utilizar sinal de = para indicar que as preferencias podem ser tomadas de maneira randômica.

* Uso de extensões em regras

	Pode-se utilizar regras "monitor" para depuração.
	Também é possível substituir listas de valores pela notação << >>

* Uso do VisualSoar para detectar erros em regras

	Menu Datamap --> check for syntax errors.

* Uso de comandos do Debugger em tempo de execução

	A maior parte está disponível através dos próprios botões da interface gráfica.

## Atividade 3

Foram implementados os comandos move e jump, incluindo seletores para decidir pela melhor pontuacao.


