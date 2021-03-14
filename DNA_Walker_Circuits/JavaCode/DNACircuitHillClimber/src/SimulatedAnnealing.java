import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SimulatedAnnealing 
{
	//Fitness
	private static double fit = 0.0;
	//Number of iterations
	private static double iter = 30000;
	//Convergence point
	private static double cp = -1;
	//x - solution - xnew new solution
	private static double xnew,x;
	//Main
	public static void main(String args[]) throws Exception
	{
		RunSA();
	}
	//Get the convergence point
	public static double GetCP() 
	{
		return(cp);
	}
	//Small change - x is a real number so change it a bit
	private static void SmallChange() 
	{
		xnew = x + (Math.random() - 0.5)/10.0;
	}
	//The main SA loop
	public static void RunSA() throws Exception
	{
		//Create a random starting point
		CreateRandomSolution();
		//elitef is the best fitness found so far, elitex is the best solution for elitef
		double elitef = ((Double.MAX_VALUE)/2.0),elitex = elitef;
		//Final cooling temperature
		double titer = 0.001; 
		//Compute starting temperature
		double t0 = ComputeTZero();
		//Compute cooling rate
		double lamda = ComputeLamda(t0,titer);
		double T = t0;
		double fdash; //new fitness
		cp = -1;
		for(int i=0;i<iter*0.99;++i)
		{
			fit = Fitness(x);
			SmallChange();
			fdash = Fitness(xnew);
			if (fdash < fit) //minimization problem
			{
				fit = fdash;
				cp = i;
				x = xnew;
			}
			else
			{
				//Compute pr(accept)
				double df =  Math.abs(fit-fdash);
				double p = Math.exp(-df/T);
				if (Math.random() < p)
				{
					//Accept worse
					fit = fdash;
					x = xnew;
					cp = i;
				}
				else
				{
					//Keep previous
					//System.out.println("UDS");
					//UnDoSwap();
				}
			}
			T *= lamda;
			if (fit < elitef)
			{
				elitef = fit;
				elitex = x;
			}
			if (i % 10 == 0) 
			{
				System.out.println(i + " " + fit + " " + elitef);
			}
		}
		fit = elitef;
		x = elitex;
	}
	//Create Random Solution
	private static void CreateRandomSolution() 
	{
		x = Math.random() * 10.0;
	}
	//Compute cooling rate
	private static double ComputeLamda(double t0, double titer) 
	{
		double lamda = (Math.log(titer) - Math.log(t0)) / (iter*0.95);
		lamda = Math.exp(lamda);
		return(lamda);
	}
	//Compute starting temperature - this is done by sampling the search space
	private static double ComputeTZero() throws Exception 
	{
		double t0 = 0.0;
		double ctziter = 0.01*iter;
		for(int i=0;i<ctziter;++i)
		{
			CreateRandomSolution();
			double f = Fitness(x);
			//if (i % 10000 == 0) System.out.println("+++"+i);
			SmallChange();
			double fdash = Fitness(xnew);
			double df = Math.abs(f-fdash);
			t0 += df;
			f = fdash;
		}
		t0 /= ctziter;
		return(t0);
	}
	private static double Fitness(double s) 
	{
		double R = 103.0;
		//Solve x * exp(x) = R
		double res = Math.abs(s *Math.exp(s) - R);
		return(res);
	}
}
