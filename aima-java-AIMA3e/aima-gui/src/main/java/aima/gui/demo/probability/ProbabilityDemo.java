package aima.gui.demo.probability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aima.core.environment.cellworld.Cell;
import aima.core.environment.cellworld.CellWorld;
import aima.core.environment.cellworld.CellWorldAction;
import aima.core.environment.cellworld.CellWorldFactory;
import aima.core.probability.CategoricalDistribution;
import aima.core.probability.FiniteProbabilityModel;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.FiniteNode;
import aima.core.probability.bayes.approx.BayesInferenceApproxAdapter;
import aima.core.probability.bayes.approx.GibbsAsk;
import aima.core.probability.bayes.approx.LikelihoodWeighting;
import aima.core.probability.bayes.approx.ParticleFiltering;
import aima.core.probability.bayes.approx.PriorSample;
import aima.core.probability.bayes.approx.RejectionSampling;
import aima.core.probability.bayes.exact.EliminationAsk;
import aima.core.probability.bayes.exact.EnumerationAsk;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.FullCPTNode;
import aima.core.probability.bayes.model.FiniteBayesModel;
import aima.core.probability.example.BayesNetExampleFactory;
import aima.core.probability.example.DynamicBayesNetExampleFactory;
import aima.core.probability.example.ExampleRV;
import aima.core.probability.example.FullJointDistributionBurglaryAlarmModel;
import aima.core.probability.example.FullJointDistributionToothacheCavityCatchModel;
import aima.core.probability.example.GenericTemporalModelFactory;
import aima.core.probability.example.HMMExampleFactory;
import aima.core.probability.example.MDPFactory;
import aima.core.probability.hmm.exact.FixedLagSmoothing;
import aima.core.probability.mdp.MarkovDecisionProcess;
import aima.core.probability.mdp.Policy;
import aima.core.probability.mdp.impl.ModifiedPolicyEvaluation;
import aima.core.probability.mdp.search.PolicyIteration;
import aima.core.probability.mdp.search.ValueIteration;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.proposition.DisjunctiveProposition;
import aima.core.probability.proposition.Proposition;
import aima.core.probability.temporal.generic.ForwardBackward;
import aima.core.probability.util.ProbabilityTable;
import aima.core.util.MockRandomizer;

/**
 * @author Ravi Mohan
 * @author Ciaran O'Reilly
 */
public class ProbabilityDemo {
	// Note: You should increase this to 1000000+
	// in order to get answers from the approximate
	// algorithms (i.e. Rejection, Likelihood and Gibbs)
	// that look close to their exact inference
	// counterparts.
	public static final int NUM_SAMPLES = 1000000;

	public static void main(String[] args) {

		// Chapter 14 - Exact
		//bayesEnumerationAskDemo();
		//bayesEliminationAskDemo();
		// Chapter 14 - Approx
		bayesRejectionSamplingDemo();
		//bayesLikelihoodWeightingDemo();
		//bayesGibbsAskDemo();


	}


	public static void bayesEnumerationAskDemo() {
		System.out.println("DEMO: Bayes Enumeration Ask");
		System.out.println("===========================");
		/*demoToothacheCavityCatchModel(new FiniteBayesModel(
				BayesNetExampleFactory.constructToothacheCavityCatchNetwork(),
				new EnumerationAsk()));*/
		/*demoBurglaryAlarmModel(new FiniteBayesModel(
				BayesNetExampleFactory.constructBurglaryAlarmNetwork(),
				new EnumerationAsk()));
		System.out.println("===========================");*/
		
		WorldCupSoccer(new FiniteBayesModel(
				BayesNetExampleFactory.constructWorldCupSoccer(),
				new EnumerationAsk()));
	}

	public static void bayesEliminationAskDemo() {
		System.out.println("DEMO: Bayes Elimination Ask");
		System.out.println("===========================");
	
		WorldCupSoccer(new FiniteBayesModel(
				BayesNetExampleFactory.constructWorldCupSoccer(),
				new EliminationAsk()));
		System.out.println("===========================");
	
	}

	public static void bayesRejectionSamplingDemo() {
		//PARA NUM_SAMPLES = 1000000
		System.out.println("DEMO: Bayes Rejection Sampling N = " + NUM_SAMPLES);
		System.out.println("==============================");
	
		WorldCupSoccer(new FiniteBayesModel(BayesNetExampleFactory.constructWorldCupSoccer(),
				new BayesInferenceApproxAdapter(new RejectionSampling(),
				NUM_SAMPLES)));
				
	}


	private static void demoToothacheCavityCatchModel(
			FiniteProbabilityModel model) {
		System.out.println("Toothache, Cavity, and Catch Model");
		System.out.println("----------------------------------");
		AssignmentProposition atoothache = new AssignmentProposition(
				ExampleRV.TOOTHACHE_RV, Boolean.TRUE);
		AssignmentProposition acavity = new AssignmentProposition(
				ExampleRV.CAVITY_RV, Boolean.TRUE);
		AssignmentProposition anotcavity = new AssignmentProposition(
				ExampleRV.CAVITY_RV, Boolean.FALSE);
		AssignmentProposition acatch = new AssignmentProposition(
				ExampleRV.CATCH_RV, Boolean.TRUE);

		// AIMA3e pg. 485
		System.out.println("P(cavity) = " + model.prior(acavity));
		System.out.println("P(cavity | toothache) = "
				+ model.posterior(acavity, atoothache));

		// AIMA3e pg. 492
		DisjunctiveProposition cavityOrToothache = new DisjunctiveProposition(
				acavity, atoothache);
		System.out.println("P(cavity OR toothache) = "
				+ model.prior(cavityOrToothache));

		// AIMA3e pg. 493
		System.out.println("P(~cavity | toothache) = "
				+ model.posterior(anotcavity, atoothache));

		// AIMA3e pg. 493
		// P<>(Cavity | toothache) = <0.6, 0.4>
		System.out.println("P<>(Cavity | toothache) = "
				+ model.posteriorDistribution(ExampleRV.CAVITY_RV, atoothache));

		// AIMA3e pg. 497
		// P<>(Cavity | toothache AND catch) = <0.871, 0.129>
		System.out.println("P<>(Cavity | toothache AND catch) = "
				+ model.posteriorDistribution(ExampleRV.CAVITY_RV, atoothache,
						acatch));
	}

	private static void demoBurglaryAlarmModel(FiniteProbabilityModel model) {
		System.out.println("--------------------");
		System.out.println("Burglary Alarm Model");
		System.out.println("--------------------");

		AssignmentProposition aburglary = new AssignmentProposition(
				ExampleRV.BURGLARY_RV, Boolean.TRUE);
		AssignmentProposition anotburglary = new AssignmentProposition(
				ExampleRV.BURGLARY_RV, Boolean.FALSE);
		AssignmentProposition anotearthquake = new AssignmentProposition(
				ExampleRV.EARTHQUAKE_RV, Boolean.FALSE);
		AssignmentProposition aalarm = new AssignmentProposition(
				ExampleRV.ALARM_RV, Boolean.TRUE);
		AssignmentProposition anotalarm = new AssignmentProposition(
				ExampleRV.ALARM_RV, Boolean.FALSE);
		AssignmentProposition ajohnCalls = new AssignmentProposition(
				ExampleRV.JOHN_CALLS_RV, Boolean.TRUE);
		AssignmentProposition amaryCalls = new AssignmentProposition(
				ExampleRV.MARY_CALLS_RV, Boolean.TRUE);

		// AIMA3e pg. 514
		System.out.println("P(j,m,a,~b,~e) = "
				+ model.prior(ajohnCalls, amaryCalls, aalarm, anotburglary,
						anotearthquake));
		System.out.println("P(j,m,~a,~b,~e) = "
				+ model.prior(ajohnCalls, amaryCalls, anotalarm, anotburglary,
						anotearthquake));

		// AIMA3e. pg. 514
		// P<>(Alarm | JohnCalls = true, MaryCalls = true, Burglary = false,
		// Earthquake = false)
		// = <0.558, 0.442>
		System.out
				.println("P<>(Alarm | JohnCalls = true, MaryCalls = true, Burglary = false, Earthquake = false) = "
						+ model.posteriorDistribution(ExampleRV.ALARM_RV,
								ajohnCalls, amaryCalls, anotburglary,
								anotearthquake));

		// AIMA3e pg. 523
		// P<>(Burglary | JohnCalls = true, MaryCalls = true) = <0.284, 0.716>
		System.out
				.println("P<>(Burglary | JohnCalls = true, MaryCalls = true) = "
						+ model.posteriorDistribution(ExampleRV.BURGLARY_RV,
								ajohnCalls, amaryCalls));

		// AIMA3e pg. 528
		// P<>(JohnCalls | Burglary = true)
		System.out.println("P<>(JohnCalls | Burglary = true) = "
				+ model.posteriorDistribution(ExampleRV.JOHN_CALLS_RV,
						aburglary));
	}
	
	public static void WorldCupSoccer(FiniteProbabilityModel model){
		System.out.println("--------------------");
		System.out.println("WorldCUpSoccer Model");
		System.out.println("--------------------");
		
		//AGE
		AssignmentProposition younger = new AssignmentProposition(
				ExampleRV.AGE_RV, "a1");
		AssignmentProposition middle = new AssignmentProposition(
				ExampleRV.AGE_RV, "a2");
		AssignmentProposition older = new AssignmentProposition(
				ExampleRV.AGE_RV, "a3");
		//NATIONALITY
		AssignmentProposition american = new AssignmentProposition(
				ExampleRV.NATIONALITY_RV, "b1");
		AssignmentProposition nonamerican = new AssignmentProposition(
				ExampleRV.NATIONALITY_RV, "b2");
		//SPORTS
		AssignmentProposition likeSoccer = new AssignmentProposition(
				ExampleRV.SPORTS_RV, "c1");
		AssignmentProposition dontlikeSoccer = new AssignmentProposition(
				ExampleRV.SPORTS_RV, "c2");
		//WATCHTV
		AssignmentProposition watchTvalot = new AssignmentProposition(ExampleRV.WATCHTV_RV, "d1");
		AssignmentProposition watchTvsome = new AssignmentProposition(ExampleRV.WATCHTV_RV, "d2");
		AssignmentProposition watchTvnone = new AssignmentProposition(ExampleRV.WATCHTV_RV, "d3");		
		
		//QUERIES
		//
		System.out.println("\nConsultas \n");
		
		System.out.printf(" P<>(non-american | younger ^ like Soccer ^  watch ) =  %.2f\n",
				+ model.prior(nonamerican, younger,likeSoccer,watchTvalot));
		
		System.out.printf(" P<>(likeSoccer | american ^  mid age ^  watch ) =  %.2f\n",
				+ model.posterior(likeSoccer,american,middle,watchTvsome));
		
		System.out.printf(" P<>(likeSoccer | older ^ watchTvnone) =  %.2f\n",
				+model.posterior(likeSoccer, older,watchTvnone));
	
	}
	
}
