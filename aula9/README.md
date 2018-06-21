**Aluno**: Ricardo Keigo de Sales Andrade

**Disciplina**: IA941A - Prof. Ricardo Gudwin

**Período**: 1o Semestre de 2018

# Relatório da Aula 9 - Clarion: Controlando o WorldServer3D

## Instruções de uso

Para rodar o código desta aula, basta abrir a pasta *aula9* no terminal e executar: `./run.sh`

A tela do World Server irá aparecer e, 3 segundos depois, o controlador Clarion será executado automaticamente, iniciando a busca pelas jóias.

## Descrição do código

Nesta atividade foi desenvolvido um controlador para um agente capaz de pegar todas as jóias indicadas em seus leaflets e parar depois disso.

A abordagem escolhida foi adaptar o programa DemoClarion, ainda utilizando ACS, e alterando o SensoryInput para direcionar as decisões do agente.

Antes de partir para essa abordagem, tentou-se selecionar a ação através de um peso diferente para cada FixedRule, mas não foi possível determinar o setup adequado para que esse peso fosse levado em conta na decisão do agente. Tentou-se ainda refinar os parâmetros relacionados a Selection Threshold e Beta de FixedRule Variavel, mas nenhuma dessas abordagens levou ao resultado desejado. Por isso, optou-se por controlar o input sensorial.

A principal modificação foi no método prepareSensoryInformation, onde se estabeleceu a priorizacao do input que ira determinar a decisão:
```
		private SensoryInformation prepareSensoryInformation(IList<Thing> listOfThings)
        {
            // New sensory information
            SensoryInformation si = World.NewSensoryInformation(CurrentAgent);

			// Detect if we have jewel ahead
			IEnumerable<Thing> jewelsAhead = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.DistanceToCreature <= 61));
			Boolean jewelAhead = jewelsAhead.Any();
			if (jewelAhead)
				jewelName = jewelsAhead.First().Name;

			// Detect if we have food ahead
			IEnumerable<Thing> foods = listOfThings.Where(item => ((item.CategoryId == Thing.CATEGORY_FOOD || item.CategoryId == Thing.CATEGORY_NPFOOD || item.CategoryId == Thing.CATEGORY_PFOOD) && item.DistanceToCreature <= 61));
			Boolean foodAhead = foods.Any();
			if (foodAhead)
				foodName = foods.First().Name;

			// save the closest food just in case the creature needs it
			IEnumerable<Thing> distantFoods = listOfThings.Where(item => ((item.CategoryId == Thing.CATEGORY_NPFOOD || item.CategoryId == Thing.CATEGORY_PFOOD) && item.DistanceToCreature > 61));
			if (distantFoods.Count() > 0) {
				closestFood = distantFoods.OrderBy(item => item.DistanceToCreature).First();
			}

			// Detect if we have a wall ahead
			Boolean wallAhead = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_BRICK && item.DistanceToCreature <= 61)).Any();

			closestJewel = null;

			//Console.WriteLine(sensorialInformation);
			Creature c = (Creature) listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_CREATURE)).First();
			int n = 0;
			bool deliver = false;
			foreach(Leaflet l in c.getLeaflets()) {
				// Detect if some leaflet is complete
				//l.situation
				mind.updateLeaflet(n,l);
				// check if we have all jewels from the leaflet (situation = true)
				if (l.situation) {
					// avoid delivering it twice
					if (!checkDelivered(l.leafletID.ToString ())) {
						leafletId = l.leafletID.ToString ();
						deliver = true;
					}
				} else {
					// lookup the closest jewel that we need to fill a leaflet
					IEnumerable<Thing> distantJewels = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.DistanceToCreature > 61 && leafletNeedsJewel(l, item)));
					if (distantJewels.Count() > 0) {
						closestJewel = distantJewels.OrderBy(item => item.DistanceToCreature).First();
					}
				}
				n++;
			}

			// Detect if we have food and we need it
			bool needAndHaveFood = (closestFood != null) && (c.Fuel < 400);

			// Add sensorial input with a rule-based prioritization

			if (checkThreeLeafletsDelivered()) {
				// Success, don't do anything else
				si.Add (inputWallAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputJewelAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputFoodAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputWallAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantFood, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantJewel, CurrentAgent.Parameters.MIN_ACTIVATION);
			} else if (jewelAhead || foodAhead) {
				// Prioritize actions that don't require move: eat food, get jewel or deliver leaflet
				double jewelAheadActivationValue = jewelAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
				double foodAheadActivationValue = foodAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
				double deliverActivationValue = deliver ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
				si.Add (inputJewelAhead, jewelAheadActivationValue);
				si.Add (inputFoodAhead, foodAheadActivationValue);
				//si.Add (inputDeliverLeaflet, deliverActivationValue);
				si.Add (inputWallAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantFood, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantJewel, CurrentAgent.Parameters.MIN_ACTIVATION);
			} else if (wallAhead) {
				// Avoid obstacle
				si.Add (inputWallAhead, CurrentAgent.Parameters.MAX_ACTIVATION);
				si.Add (inputJewelAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputFoodAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				//si.Add (inputDeliverLeaflet, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantFood, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantJewel, CurrentAgent.Parameters.MIN_ACTIVATION);
			} else if (needAndHaveFood) {
				// go for food if the creature needs it
				si.Add (inputWallAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputJewelAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputFoodAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				//si.Add (inputDeliverLeaflet, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantFood, CurrentAgent.Parameters.MAX_ACTIVATION);
				si.Add (inputDistantJewel, CurrentAgent.Parameters.MIN_ACTIVATION);
			} else if (closestJewel != null) {
				// go for the closest jewel needed for a leaflet
				si.Add (inputWallAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputJewelAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputFoodAhead, CurrentAgent.Parameters.MIN_ACTIVATION);
				//si.Add (inputDeliverLeaflet, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantFood, CurrentAgent.Parameters.MIN_ACTIVATION);
				si.Add (inputDistantJewel, CurrentAgent.Parameters.MAX_ACTIVATION);
			}
			return si;
        }
```


As Fixed Rules são as seguintes:
```
        private double FixedRuleToAvoidCollisionWall(ActivationCollection currentInput, Rule target)
        {
            // See partial match threshold to verify what are the rules available for action selection
            return ((currentInput.Contains(inputWallAhead, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
        }

		private double FixedRuleToStopWhenFinished(ActivationCollection currentInput, Rule target)
		{
			// See partial match threshold to verify what are the rules available for action selection
			return (checkThreeLeafletsDelivered() && !stopped) ? 1.0 : 0.0;
		}

		private double FixedRuleToGoToClosestJewel(ActivationCollection currentInput, Rule target)
		{
			// Here we will make the logic to go to jewel
			return ((currentInput.Contains (inputDistantJewel, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
		}

		private double FixedRuleToGoToClosestFood(ActivationCollection currentInput, Rule target)
		{
			// Here we will make the logic to go to food
			return ((currentInput.Contains(inputDistantFood, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
		}

		private double FixedRuleToEatFood(ActivationCollection currentInput, Rule target)
		{
			// Here we will make the logic to eat food
			return ((currentInput.Contains(inputFoodAhead, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
		}

		private double FixedRuleToSackJewel(ActivationCollection currentInput, Rule target)
		{
			// Here we will make the logic to sack jewel
			return ((currentInput.Contains(inputJewelAhead, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
		}

```

Quando uma jóia que não pertence a um leaflet está bloqueando o caminho, ou um alimento, eles são coletados/digeridos pelo agente.

Quando o combustivel está abaixo de 400, a coleta de alimentos ganha prioridade maior que a de jóias do leaflet.

Quando os três leaflets estão completos, o agente pára e uma mensagem de aviso é exibida.


Iniciamos o agente desenvolvido nesta atividade, junto com o agente da aula 6. O agente da aula 6 acabou ficando um pouco travado, porque não conseguia pegar algumas jóias que ele não tinha visto, mas no geral, ele tende a terminar a atividade antes.

## Conclusão

Nesta atividade foi desenvolvido, em Clarion, um agente capaz de preencher os leaflets e parar. Utilizou-se apenas o ACS, um mecanismo baseado em ações, mas esta arquitetura permite muitas abordagens diferentes, como deep learning e outras formas de aprendizado, que permitiriam uma extensão da capacidade cognitiva do agente.
