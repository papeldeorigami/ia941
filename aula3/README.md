**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Relatório da Aula 3 - Tutorial 2 do SOAR

Usando Tutorial 9.6:

https://soar.eecs.umich.edu/articles/downloads/soar-suite/228-soar-tutorial-9-6-0

_Como o estado atual da criatura é considerado nas regras?_

Regra de proposição examina o conteúdo das células adjacentes em busca de comida.
Regra de aplicação move na direção da comida.
Regra de remoção para evitar acúmulo de movimentos anteriores.

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

