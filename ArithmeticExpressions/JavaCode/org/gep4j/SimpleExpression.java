/*
 * Copyright 2010 KAT Software LLC. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.gep4j;

import java.util.ArrayList;
import java.util.List;

import org.gep4j.math.Add;
import org.gep4j.math.Divide;
import org.gep4j.math.Multiply;
import org.gep4j.math.Subtract;
import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.Probability;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.TargetFitness;

public class SimpleExpression {
	
	private static final int TRAINING_COUNT = 20;
	final KarvaEvaluator karvaEvaluator = new KarvaEvaluator();
	public INode[] bestIndividual=null;
	double expected[];
	ThreadLocal<Double> a;
	
	private void go() {
		expected = new double[TRAINING_COUNT];
		
		for (int i=0; i<TRAINING_COUNT; i++) {
			expected[i] = eval(i);
		}
		
		List<INodeFactory> factories = new ArrayList<INodeFactory>();

		factories.add(new SimpleNodeFactory(new Add()));
		factories.add(new SimpleNodeFactory(new Multiply()));
		factories.add(new SimpleNodeFactory(new Subtract()));
		factories.add(new SimpleNodeFactory(new Divide()));
		factories.add(new IntegerConstantFactory(-10, 10));
		
		a = new ThreadLocal<Double>();
		factories.add(new SimpleNodeFactory(new VariableTerminal("a", a)));

		GeneFactory factory = new GeneFactory(factories, 15);

		List<EvolutionaryOperator<INode[]>> operators = new ArrayList<EvolutionaryOperator<INode[]>>();
		operators.add(new MutationOperator<INode[]>(factory, new Probability(0.2d)));
		operators.add(new RecombinationOperator<INode[]>(factory, new Probability(0.9d)));
		EvolutionaryOperator<INode[]> pipeline = new EvolutionPipeline<INode[]>(operators);

		FitnessEvaluator<INode[]> evaluator = new FitnessEvaluator<INode[]>() {
			@Override
			public double getFitness(INode[] candidate, List<? extends INode[]> population) {
				double error = getError(candidate);
				return error;
			}

			@Override
			public boolean isNatural() {
				return false;
			}
		};

		GenerationalEvolutionEngine<INode[]> engine = new GenerationalEvolutionEngine<INode[]>(factory, pipeline, evaluator,
				new RouletteWheelSelection(), new MersenneTwisterRNG());
		
		EvolutionObserver<INode[]> observer = new EvolutionObserver<INode[]>() {
			@Override
			public void populationUpdate(PopulationData<? extends INode[]> data) {
				bestIndividual = data.getBestCandidate();
				double error = getError(bestIndividual);
				System.out.printf("Generation %d, error = %.16f, %s\n", 
								  data.getGenerationNumber(), 
								  error, 
								  karvaEvaluator.print(bestIndividual));
			}

		};
		engine.addEvolutionObserver(observer);
		engine.evolve(1000, 1, new TargetFitness(.0001, false));
		//engine.evolve(500, 1, new GenerationCount(20000));
	}

	private double getError(INode[] ind) {
		double error = 0;
		for (int i=0; i<TRAINING_COUNT; i++) {
			a.set((double) i);
			Double result = (Double) karvaEvaluator.evaluate(ind);
			error += Math.abs(expected[i] - result);
		}
		return error;
	}
	
	private double eval(double a) {
		return ((a * a) / 2) + (3 * a);
	}

	public static void main(String[] args) {
		new SimpleExpression().go();
	}

}
