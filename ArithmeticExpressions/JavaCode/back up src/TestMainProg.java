
import java.util.ArrayList;
import java.util.List;
import org.gep4j.GeneFactory;
import org.gep4j.INode;
import org.gep4j.INodeFactory;
import org.gep4j.IntegerConstantFactory;
import org.gep4j.KarvaEvaluator;
import org.gep4j.NodeEvaluation;
import org.gep4j.MutationOperator;
import org.gep4j.RecombinationOperator;
import org.gep4j.SimpleNodeFactory;
//import org.gep4j.math.Add;
//import org.gep4j.math.Divide;
import org.gep4j.math.Multiply;
//import org.gep4j.math.Subtract;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;
import org.uncommons.watchmaker.framework.EvaluatedCandidate;
import java.io.*;

public class TestMainProg {
	final KarvaEvaluator karvaEvaluator = new KarvaEvaluator();
	public INode[] bestIndividual=null;

	public void go(int i) {
		List<INodeFactory> factories = new ArrayList<INodeFactory>();
		
		// init the GeneFactory that will create the individuals
		
		//factories.add(new SimpleNodeFactory(new Add()));
		factories.add(new SimpleNodeFactory(new Multiply()));
		//factories.add(new SimpleNodeFactory(new Subtract()));
		//factories.add(new SimpleNodeFactory(new Divide()));
		//factories.add(new IntegerConstantFactory(-100000, 100000));
		factories.add(new IntegerConstantFactory(-100, 100)); //12,60,1 and the target number
		double num = 60;
		
		GeneFactory factory = new GeneFactory(factories, 10); //20 is the gene size

		List<EvolutionaryOperator<INode[]>> operators = new ArrayList<EvolutionaryOperator<INode[]>>();
		operators.add(new MutationOperator<INode[]>(factory, new Probability(0.01d)));
		//operators.add(new RecombinationOperator<INode[]>(factory, new Probability(0.5d)));
		EvolutionaryOperator<INode[]> pipeline = new EvolutionPipeline<INode[]>(operators);

		FitnessEvaluator<INode[]> evaluator = new FitnessEvaluator<INode[]>() {
			@Override
			public double getFitness(INode[] candidate, List<? extends INode[]> population) {
				//double result = (Double) karvaEvaluator.evaluate(candidate);
				//Double error = Math.abs(Math.PI - result);
				double result = (Double) karvaEvaluator.evaluate(candidate);
				double error = Math.abs(num - result);
				return error;
			}

			@Override
			public boolean isNatural() {
				return false;
			}
		};

		EvolutionEngine<INode[]> engine = new GenerationalEvolutionEngine<INode[]>(factory, pipeline, evaluator,
				new RouletteWheelSelection(), new MersenneTwisterRNG());
		
		
		// add an EvolutionObserver so we can print out the status. 		
		EvolutionObserver<INode[]> observer = new EvolutionObserver<INode[]>() {
						
			@Override
			public void populationUpdate(PopulationData<? extends INode[]> data) {
				bestIndividual = data.getBestCandidate();
//				System.out.printf("Iteration %d, Generation %d, PopulationSize = %d, error = %.1f, value = %.1f, %s\n", i,
//								  data.getGenerationNumber(), data.getPopulationSize(),
//								  Math.abs(/*Math.PI*/ num - (Double)karvaEvaluator.evaluate(bestIndividual)), 
//								  (Double)karvaEvaluator.evaluate(bestIndividual), 
//								  karvaEvaluator.print(bestIndividual));    
				
				
//				System.out.printf("%d, %d, %d, %.1f, %.1f, %s\n", i,
//						  data.getGenerationNumber(), data.getPopulationSize(),
//						  Math.abs(/*Math.PI*/ num - (Double)karvaEvaluator.evaluate(bestIndividual)), 
//						  (Double)karvaEvaluator.evaluate(bestIndividual), 
//						  karvaEvaluator.print(bestIndividual));    
				
					try{
						
						String fileName = "Target_"+ num +"_pop_10_Iteration_1000_opr_mult"+".txt";
						File file = new File(fileName); //Your file
						FileOutputStream fos = new FileOutputStream(file,true);
						PrintStream ps = new PrintStream(fos);
						System.setOut(ps);
						System.out.printf("%d, %d, %d, %.1f, %.1f, %s\n", i,
								  data.getGenerationNumber(), data.getPopulationSize(),
								  Math.abs(/*Math.PI*/ num - (Double)karvaEvaluator.evaluate(bestIndividual)), 
								  (Double)karvaEvaluator.evaluate(bestIndividual), 
								  karvaEvaluator.print(bestIndividual));    
						
					}catch(Exception e){
						
					}
				
				
				
				
				
				/* ******Retrieving population data for every generation****** */
				
//				data.getevaluatedPopulation().forEach((temp) -> {
//					   System.out.println(karvaEvaluator.print(temp.getCandidate()));
//					  });
//				
//				String fileName = "D:\\workspace\\TestGEPSource\\Gen" + data.getGenerationNumber() + ".csv";
//				try{
//					PrintWriter writer = new PrintWriter(fileName,"UTF-8");
//					
//					data.getevaluatedPopulation().forEach((temp) -> {
//						   writer.println(karvaEvaluator.print(temp.getCandidate()));
//						  });				
//					writer.close();
//				}catch (IOException e) {
//					// do something
//				}
				
				/* *************End of retrieving population data for every generation********** */
				
								
			}
			
			
			
		};
		engine.addEvolutionObserver(observer);
		
		// run it with 1000 population size, 1 elite individual, 0 target fitness.
		engine.evolve(10, 1, new TargetFitness(0.0001, false));
	
		
		//to get the total population
//		List<EvaluatedCandidate<INode[]>> populationList = engine.evolvePopulation(1000,100,new TargetFitness(0.1, false));
//		populationList.forEach((temp) -> {
//			   System.out.println(karvaEvaluator.print(temp.getCandidate()));
//			  });
		
		
//		List<EvaluatedCandidate<INode[]>> popList = engine.printPopulation(1000, 100);
//		popList.forEach((temp) -> {
//			   System.out.println(karvaEvaluator.print(temp.getCandidate()));
//			  });
	}
	
	public static final void main(String args[]) {
		
		for(int i = 0; i< 1000; i++){
			
			new TestMainProg().go(i);				
		}
		
		
		
	}
}
