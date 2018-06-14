**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Aula 14 - CST Controlando o WorldServer3D

O codigo-fonte das atividades desenvolvidas pelo aluno encontra-se disponivel em:
https://github.com/papeldeorigami/ia941

## Atividade

O tutorial disponível em [http://cst.fee.unicamp.br/tutorials/CoreModelTrail] foi estudado. Seguem algumas anotações de estudo:
- O conceito de Mind do CST se divide basicamente entre Raw Memory, com os Memory Objects (MO) e CodeRack, com os Codelets.
- Um Codelet tem, basicamente, 3 blocos: a) proc, onde roda o código principal b) accessMemoryObjects, onde acessa MO de input e output e c) calculateActivation, para fazer broadcast de MO especificando o valor de ativacao
- No problema do WorldServer3D, modelamos alguns objetos de memória perceptual (maçãs disponiveis e maçãs próximas),
elementos de memória sensorial (visão e sensores internos) e elementos de memória motora (indicando ações para as mãos - tais como pegar, esconder - e para movimentação). De maneira análoga,
definimos Codelets sensoriais, para controlar a visão e os sensores internos (posição, combustível, etc.); codelets de percepção para detecção de maçãs enxergadas e maçãs próximas;
Codelets de comportamento (para definir as ações) e Codelets motores - para executar as ações.

O programa DemoCST (WS3DApp) foi baixado do github em [https://github.com/CST-Group/WS3DApp].

Para estender o DemoCST de maneira a atingir a meta de completar um leaflet, vamos precisar:
1) Incluir o leaflet nos sensores internos
2) Estender os de percepção para a) salvar em memória as jóias enxergadas e b) identificar as jóias próximas
3) Criar codelets de comportamento para a) ir atrás das jóias que completam o leaflet, b) pegar as jóias próximas e c) entregar leaflets
4) Estender o codelet motor das mãos para a) pegar jóias e b) entregar leaflets
