using System;
using System.Collections.Generic;
using System.Linq;
using System.Globalization;
using Clarion;
using Clarion.Framework;
using Clarion.Framework.Core;
using Clarion.Framework.Templates;
using ClarionApp.Model;
using ClarionApp;
using System.Threading;
using Gtk;

namespace ClarionApp
{
    /// <summary>
    /// Public enum that represents all possibilities of agent actions
    /// </summary>
    public enum CreatureActions
    {
        DO_NOTHING,
        ROTATE_CLOCKWISE,
        GO_AHEAD,
		GO_TO,
		EAT_FOOD,
		SACK_JEWEL,
		DELIVER_LEAFLET,
		STOP
    }

    public class ClarionAgent
    {
        #region Constants
        /// <summary>
        /// Constant that represents the Visual Sensor
        /// </summary>
        private String SENSOR_VISUAL_DIMENSION = "VisualSensor";
        /// <summary>
        /// Constant that represents that there is at least one wall ahead
        /// </summary>
        private String DIMENSION_WALL_AHEAD = "WallAhead";
		/// <summary>
		/// Constant that represents that there is at least one food ahead
		/// </summary>
		private String DIMENSION_FOOD_AHEAD = "FoodAhead";
		/// <summary>
		/// Constant that represents that there is at least one jewel ahead
		/// </summary>
		private String DIMENSION_JEWEL_AHEAD = "JewelAhead";
		/// <summary>
		/// Constant that represents that some leaflet is ready to be delivered
		/// </summary>
		private String DIMENSION_DELIVER_LEAFLET = "DeliverLeaflet";

		double prad = 0;
        #endregion

        #region Properties
		public MindViewer mind;
		String creatureId = String.Empty;
		String creatureName = String.Empty;
		String foodName = String.Empty;
		String jewelName = String.Empty;
		String leafletId = String.Empty;
		Thing closestFood = null;
		Thing closestJewel = null;
		Dictionary<string, bool> deliveredLeaflets = new Dictionary<string, bool>();
		bool stopped = false;
		#region Simulation
        /// <summary>
        /// If this value is greater than zero, the agent will have a finite number of cognitive cycle. Otherwise, it will have infinite cycles.
        /// </summary>
        public double MaxNumberOfCognitiveCycles = -1;
        /// <summary>
        /// Current cognitive cycle number
        /// </summary>
        private double CurrentCognitiveCycle = 0;
        /// <summary>
        /// Time between cognitive cycle in miliseconds
        /// </summary>
        public Int32 TimeBetweenCognitiveCycles = 0;
        /// <summary>
        /// A thread Class that will handle the simulation process
        /// </summary>
        private Thread runThread;
        #endregion

        #region Agent
		private WSProxy worldServer;
        /// <summary>
        /// The agent 
        /// </summary>
        private Clarion.Framework.Agent CurrentAgent;
        #endregion

        #region Perception Input
        /// <summary>
        /// Perception input to indicates a wall ahead
        /// </summary>
		private DimensionValuePair inputWallAhead;
		/// <summary>
		/// Perception input to indicates food ahead
		/// </summary>
		private DimensionValuePair inputFoodAhead;
		/// <summary>
		/// Perception input to indicates jewel ahead
		/// </summary>
		private DimensionValuePair inputJewelAhead;
		/// <summary>
		/// Perception input to indicate ready to deliver a leaflet
		/// </summary>
		private DimensionValuePair inputDeliverLeaflet;
        #endregion

        #region Action Output
        /// <summary>
        /// Output action that makes the agent to rotate clockwise
        /// </summary>
		private ExternalActionChunk outputRotateClockwise;
        /// <summary>
        /// Output action that makes the agent go ahead
        /// </summary>
		private ExternalActionChunk outputGoAhead;
		/// <summary>
		/// Output action that makes the agent eat food
		/// </summary>
		private ExternalActionChunk outputEatFood;
		/// <summary>
		/// Output action that makes the agent sack a jewel
		/// </summary>
		private ExternalActionChunk outputSackJewel;
		/// <summary>
		/// Output action to deliver a leaflet
		/// </summary>
		private ExternalActionChunk outputDeliverLeaflet;
		/// <summary>
		/// Output action to stop the agent
		/// </summary>
		private ExternalActionChunk outputStop;
		/// <summary>
		/// Output action to go to a target position
		/// </summary>
		private ExternalActionChunk outputGoTo;
        #endregion

        #endregion

        #region Constructor
		public ClarionAgent(WSProxy nws, String creature_ID, String creature_Name)
        {
			worldServer = nws;
			// Initialize the agent
            CurrentAgent = World.NewAgent("Current Agent");
			mind = new MindViewer();
			mind.Show ();
			creatureId = creature_ID;
			creatureName = creature_Name;

            // Initialize Input Information
            inputWallAhead = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_WALL_AHEAD);
			inputFoodAhead = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_FOOD_AHEAD);
			inputJewelAhead = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_JEWEL_AHEAD);
			inputDeliverLeaflet = World.NewDimensionValuePair(SENSOR_VISUAL_DIMENSION, DIMENSION_DELIVER_LEAFLET);

            // Initialize Output actions
            outputRotateClockwise = World.NewExternalActionChunk(CreatureActions.ROTATE_CLOCKWISE.ToString());
            outputGoAhead = World.NewExternalActionChunk(CreatureActions.GO_AHEAD.ToString());
			outputGoTo = World.NewExternalActionChunk(CreatureActions.GO_TO.ToString());
			outputEatFood = World.NewExternalActionChunk(CreatureActions.EAT_FOOD.ToString());
			outputSackJewel = World.NewExternalActionChunk(CreatureActions.SACK_JEWEL.ToString());
			outputDeliverLeaflet = World.NewExternalActionChunk(CreatureActions.DELIVER_LEAFLET.ToString());
			outputStop = World.NewExternalActionChunk(CreatureActions.STOP.ToString());

            //Create thread to simulation
            runThread = new Thread(CognitiveCycle);
			Console.WriteLine("Agent started");
        }
        #endregion

        #region Public Methods
        /// <summary>
        /// Run the Simulation in World Server 3d Environment
        /// </summary>
        public void Run()
        {                
			Console.WriteLine ("Running ...");
            // Setup Agent to run
            if (runThread != null && !runThread.IsAlive)
            {
                SetupAgentInfraStructure();
				// Start Simulation Thread                
                runThread.Start(null);
            }
        }

        /// <summary>
        /// Abort the current Simulation
        /// </summary>
        /// <param name="deleteAgent">If true beyond abort the current simulation it will die the agent.</param>
        public void Abort(Boolean deleteAgent)
        {   Console.WriteLine ("Aborting ...");
            if (runThread != null && runThread.IsAlive)
            {
                runThread.Abort();
            }

            if (CurrentAgent != null && deleteAgent)
            {
                CurrentAgent.Die();
            }
        }

		IList<Thing> processSensoryInformation()
		{
			IList<Thing> response = null;

			if (worldServer != null && worldServer.IsConnected)
			{
				response = worldServer.SendGetCreatureState(creatureName);
				prad = (Math.PI / 180) * response.First().Pitch;
				while (prad > Math.PI) prad -= 2 * Math.PI;
				while (prad < - Math.PI) prad += 2 * Math.PI;
				Sack s = worldServer.SendGetSack("0");
				mind.setBag(s);
			}

			return response;
		}

		void processSelectedAction(CreatureActions externalAction)
		{   Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");
			if (worldServer != null && worldServer.IsConnected)
			{
				switch (externalAction)
				{
				case CreatureActions.DO_NOTHING:
					// Do nothing as the own value says
					break;
				case CreatureActions.ROTATE_CLOCKWISE:
					worldServer.SendSetAngle(creatureId, 2, -2, 2);
					break;
				case CreatureActions.GO_TO:					
					worldServer.SendSetGoTo(creatureId, 1, 1, gotoX, gotoY);
					break;
				case CreatureActions.GO_AHEAD:
					worldServer.SendSetAngle(creatureId, 1, 1, prad);
					break;
				case CreatureActions.EAT_FOOD:
					worldServer.SendEatIt (creatureId, foodName);
					Console.WriteLine("Eat food " + foodName);
					break;
				case CreatureActions.SACK_JEWEL:
					worldServer.SendSackIt (creatureId, jewelName);
					Console.WriteLine("Sack jewel " + jewelName);
					break;
				case CreatureActions.DELIVER_LEAFLET:
					worldServer.SendDeliverIt (creatureId, leafletId);
					Console.WriteLine ("Delivered " + leafletId);
					deliveredLeaflets[leafletId] = true;
					break;
				case CreatureActions.STOP:
					worldServer.SendStopCreature (creatureId);
					Console.WriteLine ("Stop the creature");
					stopped = true;
					break;
				default:
					break;
				}
			}
		}

        #endregion

        #region Setup Agent Methods
        /// <summary>
        /// Setup agent infra structure (ACS, NACS, MS and MCS)
        /// </summary>
        private void SetupAgentInfraStructure()
        {
            // Setup the ACS Subsystem
            SetupACS();                    
        }

        private void SetupMS()
        {            
            //RichDrive
        }

        /// <summary>
        /// Setup the ACS subsystem
        /// </summary>
        private void SetupACS()
        {
			// Create Rule To Stop when all leaflets have been delivered (success)
			SupportCalculator stopWhenFinishedSupportCalculator = FixedRuleToStopWhenFinished;
			FixedRule ruleStopWhenFinished = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputStop, stopWhenFinishedSupportCalculator);

			// Commit this rule to Agent (in the ACS)
			CurrentAgent.Commit(ruleStopWhenFinished);

			// Create Rule to avoid colision with wall
            SupportCalculator avoidCollisionWallSupportCalculator = FixedRuleToAvoidCollisionWall;
            FixedRule ruleAvoidCollisionWall = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputRotateClockwise, avoidCollisionWallSupportCalculator);

            // Commit this rule to Agent (in the ACS)
            CurrentAgent.Commit(ruleAvoidCollisionWall);

            // Create Rule To Go Ahead
            SupportCalculator goAheadSupportCalculator = FixedRuleToGoAhead;
            FixedRule ruleGoAhead = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputGoAhead, goAheadSupportCalculator);

			// Commit this rule to Agent (in the ACS)
			CurrentAgent.Commit(ruleGoAhead);

			// Create Rule to Eat Food
			SupportCalculator eatFoodSupportCalculator = FixedRuleToEatFood;
			FixedRule ruleEatFood = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputEatFood, eatFoodSupportCalculator);

            // Commit this rule to Agent (in the ACS)
			CurrentAgent.Commit(ruleEatFood);

			// Create Rule to Collect (Sack) Jewel
			SupportCalculator sackJewelSupportCalculator = FixedRuleToSackJewel;
			FixedRule ruleSackJewel = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputSackJewel, sackJewelSupportCalculator);

			// Commit this rule to Agent (in the ACS)
			CurrentAgent.Commit(ruleSackJewel);

			// Create Rule to Deliver a Leaflet
			SupportCalculator deliverLeafletSupportCalculator = FixedRuleToDeliverLeaflet;
			FixedRule ruleDeliverLeaflet = AgentInitializer.InitializeActionRule(CurrentAgent, FixedRule.Factory, outputDeliverLeaflet, deliverLeafletSupportCalculator);

			// Commit this rule to Agent (in the ACS)
			CurrentAgent.Commit(ruleDeliverLeaflet);

            // Disable Rule Refinement
            CurrentAgent.ACS.Parameters.PERFORM_RER_REFINEMENT = false;

            // The selection type will be probabilistic
            CurrentAgent.ACS.Parameters.LEVEL_SELECTION_METHOD = ActionCenteredSubsystem.LevelSelectionMethods.STOCHASTIC;

            // The action selection will be fixed (not variable) i.e. only the statement defined above.
            CurrentAgent.ACS.Parameters.LEVEL_SELECTION_OPTION = ActionCenteredSubsystem.LevelSelectionOptions.FIXED;

            // Define Probabilistic values
            CurrentAgent.ACS.Parameters.FIXED_FR_LEVEL_SELECTION_MEASURE = 1;
            CurrentAgent.ACS.Parameters.FIXED_IRL_LEVEL_SELECTION_MEASURE = 0;
            CurrentAgent.ACS.Parameters.FIXED_BL_LEVEL_SELECTION_MEASURE = 0;
            CurrentAgent.ACS.Parameters.FIXED_RER_LEVEL_SELECTION_MEASURE = 0;
        }

		// helper routine just to check if some leaflet has already been delivered
		private bool checkDelivered(string leafletId) {
			bool value = false;
			if (!deliveredLeaflets.TryGetValue(leafletId, out value))
				return false;
			return value;
		}

		// helper routine to check if all the three leaflets have been delivered
		private bool checkThreeLeafletsDelivered() {
			return (deliveredLeaflets.Where(item => item.Value).Count() == 3);
		}

		private bool leafletNeedsJewel(Leaflet l, Thing jewel) {
		}

        /// <summary>
        /// Make the agent perception. In other words, translate the information that came from sensors to a new type that the agent can understand
        /// </summary>
        /// <param name="sensorialInformation">The information that came from server</param>
        /// <returns>The perceived information</returns>
		private SensoryInformation prepareSensoryInformation(IList<Thing> listOfThings)
        {
            // New sensory information
            SensoryInformation si = World.NewSensoryInformation(CurrentAgent);

			// Detect if we have jewel ahead
			IEnumerable<Thing> jewelsAhead = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.DistanceToCreature <= 61));
			Boolean jewelAhead = jewelsAhead.Any();
			if (jewelAhead)
				jewelName = jewelsAhead.First().Name;
			double jewelAheadActivationValue = jewelAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
			si.Add(inputJewelAhead, jewelAheadActivationValue);

			// Detect if we have food ahead
			IEnumerable<Thing> foods = listOfThings.Where(item => ((item.CategoryId == Thing.CATEGORY_NPFOOD || item.CategoryId == Thing.categoryPFOOD) && item.DistanceToCreature <= 61));
			Boolean foodAhead = foods.Any();
			if (foodAhead)
				foodName = foods.First().Name;
			double foodAheadActivationValue = foodAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
			si.Add(inputFoodAhead, foodAheadActivationValue);

			// Detect if we have a wall ahead
			Boolean wallAhead = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_BRICK && item.DistanceToCreature <= 61)).Any();
			double wallAheadActivationValue = wallAhead ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
			si.Add(inputWallAhead, wallAheadActivationValue);

			//Console.WriteLine(sensorialInformation);
			Creature c = (Creature) listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_CREATURE)).First();
			int n = 0;
			bool deliver = false;
			foreach(Leaflet l in c.getLeaflets()) {
				// Detect if some leaflet is complete
				//l.situation
				mind.updateLeaflet(n,l);
				if (l.situation) {
					// avoid delivering it twice
					if (!checkDelivered (l.leafletID.ToString ())) {
						leafletId = l.leafletID.ToString ();
						deliver = true;
					}
				} else {
					// lookup the closest jewel that we need to fill a leaflet
					// TODO wip
					IEnumerable<Thing> nextJewelsFromLeaflets = listOfThings.Where(item => (item.CategoryId == Thing.CATEGORY_JEWEL && item.DistanceToCreature > 60 && leafletNeedsJewel(l, item)));

				}
				n++;
			}
			double deliverActivationValue = deliver ? CurrentAgent.Parameters.MAX_ACTIVATION : CurrentAgent.Parameters.MIN_ACTIVATION;
			si.Add(inputDeliverLeaflet, deliverActivationValue);
            return si;
        }
        #endregion

        #region Fixed Rules
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

		private double FixedRuleToGoAhead(ActivationCollection currentInput, Rule target)
        {
            // Here we will make the logic to go ahead
			return ((currentInput.Contains(inputWallAhead, CurrentAgent.Parameters.MIN_ACTIVATION)) && !checkThreeLeafletsDelivered()) ? 1.0 : 0.0;
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

		private double FixedRuleToDeliverLeaflet(ActivationCollection currentInput, Rule target)
		{
			// Here we will make the logic to deliver a leaflet
			return ((currentInput.Contains(inputDeliverLeaflet, CurrentAgent.Parameters.MAX_ACTIVATION))) ? 1.0 : 0.0;
		}
		#endregion

        #region Run Thread Method
        private void CognitiveCycle(object obj)
        {

			Console.WriteLine("Starting Cognitive Cycle ... press CTRL-C to finish !");
            // Cognitive Cycle starts here getting sensorial information
            while (CurrentCognitiveCycle != MaxNumberOfCognitiveCycles)
            {   
				// Get current sensory information                    
				IList<Thing> currentSceneInWS3D = processSensoryInformation();

                // Make the perception
                SensoryInformation si = prepareSensoryInformation(currentSceneInWS3D);

                //Perceive the sensory information
                CurrentAgent.Perceive(si);

                //Choose an action
                ExternalActionChunk chosen = CurrentAgent.GetChosenExternalAction(si);

                // Get the selected action
                String actionLabel = chosen.LabelAsIComparable.ToString();
                CreatureActions actionType = (CreatureActions)Enum.Parse(typeof(CreatureActions), actionLabel, true);

                // Call the output event handler
				processSelectedAction(actionType);

                // Increment the number of cognitive cycles
                CurrentCognitiveCycle++;

                //Wait to the agent accomplish his job
                if (TimeBetweenCognitiveCycles > 0)
                {
                    Thread.Sleep(TimeBetweenCognitiveCycles);
                }
			}
        }
        #endregion

    }
}
