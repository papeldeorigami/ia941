**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Aula 12 - LIDA

O codigo-fonte das atividades desenvolvidas pelo aluno encontra-se disponivel em:
https://github.com/papeldeorigami/ia941

## Atividade 1

Tutorial e exercícios foram baixados e configurados na máquina.

## Atividade 2

Foi feita a leitura do tutorial e dos slides disponibilizados.

Os exercicios do basicAgentExercises foram realizados.

Respostas aos study questions:

1.1. The perceptual buffer is in the workspace module. Nodes come from the environment, once detectors, colaitions and listeners are in place.

1.2. Coalitions come from the detectors and they are handled by tasks

1.3. The Current Situational Model includes past sensory information that were in the Perceptual Buffer; both come from the PAM, once detected by the agent.

Atividade 3

Respostas aos study questions:

2.1. Esta GUI é significativamente mais elaborada do que aquela do basicAgentExercises. Enquanto esta apresentava apenas dois botões e um objeto, aquela apresenta
uma multiplicidade de objetos, um grid bidimensional para movimentação do agente e diversos elementos para examinar o mundo, tais como o drop down e a possibilidade
de clicar em objetos.

2.2. Agora o agente "percebe", através das novas features e tarefas, quando um predador está diretamente em frente a ele, ou o nível de saúde está baixo. Essas ações acontecem porque essas novas percepções são ativadas e transferidas para o buffer de percepção.

2.3. Algumas razões possíveis para o agente não fugir do predador: ausência de detectores de percepção, falta de definição de tarefas, falta de memória procedural ou a ausência de um codelet de atenção que gere as coligações (coalitions)

2.4. Agora uma coligação é gerada toda vez que um predador está na vizinhança do agente, permitindo a definição de ações.

2.5. O nível de ativação ficou muito baixo para a comida, e praticamente sempre ele vai perder para outros estímulos, como por exemplo, uma frente vazia.
1.3. The Current Situational Model includes past sensory information that were in the Perceptual Buffer; both come from the PAM, once detected by the agent.
