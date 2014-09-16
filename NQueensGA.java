import java.util.ArrayList;
import java.util.Random;


public class NQueensGA {

	
	private static int POP_SIZE; 	// Size of the population to be used
	private static int BOARD_SIZE; 	// Size of the board i.e. number of queens
	private static double MUTATION_RATE; 
	private static int NO_PARENT; 	// Number of offspring in each generation
	private static int NO_GEN; // Maximum number of gens to be run	

	private static int RUN_COUNT;	// Number of times to run algorithm
	private static int PARENT_SEL;	// Which parent selection method to use
	private static int CROSS_OVER;	// Which parent crossover method to use

	public static boolean complete = false;	// True when solution is found
	public static int parIndex;				// Index used for parent selection
	private static int EPOCHS;				// Number of generations elapsed

	private static int avEpoch = 0;
	private static int avCost = 0;
	private static int max = 0;

	private static ArrayList<Board> population = null; // Population of boards


	/* 	Main method, handles command line inputs for setting class variables
	 *	and invoking the genetic algorithm. 
	 */	
	public static void main(String[] args) {
		// Usage BOARD_SIZE, POP_SIZE, NO_PARENTS, NO_GENERATIONS
		// MUTATION_RATE, RUN_COUNT, PARENT_SELECT, CROSSOVER
			try {
				BOARD_SIZE 		= Integer.parseInt(args[0]);
				POP_SIZE 		= Integer.parseInt(args[1]);
				NO_PARENT 		= Integer.parseInt(args[2]);
				NO_GEN 			= Integer.parseInt(args[3]);
				MUTATION_RATE	= Double.parseDouble(args[4]);
				RUN_COUNT 		= Integer.parseInt(args[5]);
				PARENT_SEL 		= Integer.parseInt(args[6]);
				CROSS_OVER 		= Integer.parseInt(args[7]);
				

			} catch (NumberFormatException e) {
				System.err.println("Argument " + args[0]+ " must be an integer or double for mutation rate");
				System.exit(1);
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Usage: \nBOARD_SIZE, POP_SIZE, NO_PARENTS, NO_GENERATIONS, \nMUTATION_RATE, RUN_COUNT, PARENT_SELECT, CROSSOVER");
				System.exit(1);
			}
	
			System.out.print("\n\nBoard Size : " + BOARD_SIZE + " : ");
			System.out.print("Population Size : " + POP_SIZE + " : ");
			System.out.print("Number Parent : " + NO_PARENT + " : ");
			System.out.print("\nMax Num Generation : " + NO_GEN + " : ");
			System.out.print("Mutation Rate : " + MUTATION_RATE + " : ");
			System.out.print("Run Count : " + RUN_COUNT + " \n");
			System.out.print("Parent Type : " + PARENT_SEL + " : ");
			System.out.print("Cross Type : " + CROSS_OVER + " \n\n");
			

		// Run algorithm for RUN_COUNT iterations
		for (int i = 0; i < RUN_COUNT; i++) {
			EPOCHS = 0;
			complete = false;
			population = new ArrayList<Board>();
			geneticAlgorithm();
			System.out.println("Pop:\t" + (i+1) + "\t\t Count:\t" + EPOCHS);
			if (EPOCHS != NO_GEN) avEpoch += EPOCHS;
			else max++;

		}
		System.out.println("Average Epochs: " + avEpoch / RUN_COUNT);
		System.out.println("Max: " + max);
	}

	/*
	 * 	Genetic Algorithm begins here, initialisation, sorting and looping 
	 * 	take place here along with checking the stopping condition.
	 */
	public static void geneticAlgorithm() {
	
		initPop(); 	// Initalise the population then evaluate all members.
		sort();		// Sort the population based on each members cost.
		//System.out.println(population.get(0).cost());
		//System.out.println(avCost/POP_SIZE);
		// Until a solution (member with cost = 0) is found, do:
		while (!complete) {
			avCost = 0;
			reproduce(); 	// Take two parents and create two offspring 
			sort(); 		// Sort the population based on each members cost
			//System.out.println(population.get(0).cost());
			//System.out.println(avCost/POP_SIZE);
			if (EPOCHS >= NO_GEN) break; // If at generation limit.
			else EPOCHS++;
		}
	}

	
	/* 	Initalise the Population - For each member of the population, create a
	 *	new Board with random queen pieces. Then evaluate the board to find 
	 * 	the fitness of the board. Then add the board to the population. 
	 */
	static void initPop() {
		Board newBoard = null; 
		for (int g = 0; g < POP_SIZE; g++) {
			newBoard = new Board();
        	newBoard.evalBoard();
        	population.add(newBoard);
    	}
	}
	
	/* 	Reproduce population - Take the current population and select parents
	 * 	to mate and create new offspring. This is done by crossing over two 
	 * 	selected parents (parents have the better (smaller) cost) and the 
	 * 	result is two child offspring which are then put into the population.
	 *
	 * 	Case 0: Single fixed-point crossover
	 *	Case 1: Single random-point crossover
	 *	Case 2: Double fixed-points crossover
	 *	Case 3: Double random-points crossover
	 */
	static void reproduce() {
		int p1 = 0,  p2 = 0;
		Random rn = new Random();
		parIndex = 1;
		
		/* For all of the non-parent members of the population, replace with 
         * offspring of parent breeding 
         */
		for (int i = NO_PARENT; i <= POP_SIZE - 1; i += 2) {
			Board ch1 = new Board();
			Board ch2 = new Board();

			Board par1, par2 = null;

			// Support for parent selections - (2)Alpha-male and (3)pairs
			if (PARENT_SEL == 2) {
				par1 = population.get(0);
				par2 = genParent();
			} if (PARENT_SEL == 3) {
				par1 = genParent();
				par2 = population.get(population.indexOf(par1) + 1);
			} else {
				par1 = genParent();
				par2 = genParent();
			}

			// Ensure two parents are not same (at same index)
			while (population.indexOf(par1) == population.indexOf(par2)) {
				par1 = population.get(rn.nextInt(NO_PARENT + 1));
			}

			// Switch for selecting crossover 
			switch(CROSS_OVER) {
				case 0: 
						// Single point, fixed position
						ch1.vector(par2.crossBoard(par1, 0, BOARD_SIZE/2));
        				ch2.vector(par1.crossBoard(par2, 0, BOARD_SIZE/2));
						break;
				case 1: 
						// Single point, random position
						p1 = 1 + rn.nextInt(BOARD_SIZE - 1);
						ch1.vector(par2.crossBoard(par1, 0, p1));
        				ch2.vector(par1.crossBoard(par2, 0, p1));
						break;
				case 2:
						// Multiple point, fixed position
						ch1.vector(par2.crossBoard(par1, 2, 5));
        				ch2.vector(par1.crossBoard(par2, BOARD_SIZE/3,
                                                   BOARD_SIZE/3 * 2));
						break;
				case 3: 
						// Multiple point, random position
						p1 = 1 + rn.nextInt(BOARD_SIZE - 1);
						p2 = 1 + rn.nextInt(BOARD_SIZE - 1);
						int min = Math.min(p1, p2), max = Math.max(p1, p2);

						ch1.vector(par1.crossBoard(par2, min, max));
        				ch2.vector(par2.crossBoard(par1, min, max));
						break;
			}

			// Mutate the two children
        	ch1.mutateBoard();
        	ch2.mutateBoard();

        	// Evaluate the cost of the two children
        	complete = ch1.evalBoard();
        	complete = ch2.evalBoard();
        	
        	// Insert the two children ito the population
        	population.set(i, ch1);
        	population.set(i + 1, ch2);
    	}
	}

 	/* 	Generate a parent to be used for creating new offspring.
 	 * 	Depending on which case is selected, a parent is chosen from the 
 	 * 	parent population and is returned. 
 	 * 	
	 *	@return Board - The selected parent
 	 *
	 * 	Case 0: Superior parent selcetion - binary tournment (Best of two 
	 * 	parents)
	 *	Case 1: Random member from the parent population is returned
	 *	Case 2: Alpha-male, the best parent is mated with all other parents 
	 *	Case 3: Mate pairs of population members - 0 with 1, 2 with 3, etc 
	 */
	static Board genParent() {
		Random rn = new Random();

		switch (PARENT_SEL) {
			case 0:
					// Generate two random numbers in the parent population 
					int r1 = rn.nextInt(NO_PARENT + 1);
					int r2 = rn.nextInt(NO_PARENT + 1);
		
					// Compare the fitness values of the two random boards 
					int f1 = population.get(r1).cost(); 
					int f2 = population.get(r2).cost();

					// Return the better of the two parents
					return f1 < f2 ? population.get(r1) : population.get(r2);
			case 1:
					// Return a random parent from the parent population
					return population.get(rn.nextInt(NO_PARENT + 1));
			case 2:
					// Return the next parent to be mated with the alpha-male
					if (parIndex == NO_PARENT + 1) parIndex = 1;
					return population.get(parIndex++);
			case 3:
					// Return the first 
					if (parIndex == NO_PARENT + 1) parIndex = 1;
					parIndex += 2;
					return population.get(parIndex - 3);

			default: return population.get(rn.nextInt(NO_PARENT + 1));
					
		}
	}


	/*
	 * 	Sort the population based on the cost of each member.
	 * 	Sorted from lowest cost to highest cost, as the lower the cost
	 * 	the better the member (solution) is.
	 */
	static void sort() {
		// Selection sort is used to sort the members in the population
    	for (int i = 0; i < POP_SIZE; i++) {
       		int first = i;
        	for (int j = i + 1; j < POP_SIZE; j++) {
            	if (population.get(j).cost() < population.get(first).cost()) {
                    first = j;
            	}
        	}
        	Board temp = population.get(first);
        	population.set(first, population.get(i));
        	population.set(i, temp);
    	}
    return;
	}


	/*
	 * 	Print out the population of members.
	 * 	Iteratively goes through all members vectors and iterates over all 
	 * 	Queen pieces to print out all of their values.
	 */
	static void print() {

		for (int i = 0; i < POP_SIZE; i++) {
			Board temp = population.get(i);
			System.out.print("Board : " + i + "\t [ ");

            for (int j = 0; j < BOARD_SIZE; j++) {
				System.out.print(temp.vector()[j] + ", ");             }
				System.out.println("\t Cost : " + population.get(i).cost() 
															+ "  ");         }
				System.out.println("\n\n");     }



	/*
	 * 	Board class is used to hold data and methods that relate to the board 
	 * 	(set of queens in vector).
	 */
	private static class Board {

		private int vector[] = new int[BOARD_SIZE];//Vector for queen positions
		private int cost = 0; 					   // Cost of the vector


		/*
		 *	Initaise the board (vector) with random queen positions with 
		 * 	values between 1 and 8.
		 */
		public Board() {
			for (int i = 0; i < BOARD_SIZE; i++) { 
				int min = 1, max = 8;
				this.vector[i] = min + (int)(Math.random() * ((max-min) + 1));
			}
		}

		/*
		 *	Evaluates the board (vector) to calculate the fitness of the
		 * 	board vector. 
		 * 	Cost is calculated by checking for colisions on the horizontal 
		 *	axis, and both the upper and lower diagonal axes for each posioin 
		 * 	in the vector.
		 *	Checking out of bounds in the y axis is not implemented, as their 
		 *	is no risk of indexing outof bounds as we are only comparing two 
		 *	integer values. This simplifies code.
		 */
		public boolean evalBoard() {
			cost = 0;

			// For each position in the vector, check for collisions
			for (int index = 0; index < BOARD_SIZE; index++) { 
				int cur = vector[index], dia = 1;

				for (int i = index + 1; i < BOARD_SIZE; i++) {
					if (cur == vector[i]) cost++; 		// Horizontal
					if (cur + dia == vector[i]) cost++; // Up diagonal
					if (cur - dia == vector[i]) cost++; // Down daigonal
					dia++;
				}	
			}	
				avCost += cost;
				return cost == 0 ? true : complete;
		}

		/*
		 * 	Crossover two parents at given points to generate a new offspring.
		 * 	NOTE: Call method twice (with parents inverted) to get two 
		 * 	offspring.
		 *	@param Board - Parent Board to be crossed with current Board.
		 * 	@param int - Cross point one - First point to crossover
		 * 	@param int - Cross point two - Second point to crossover.
		 */
		public int[] crossBoard(Board board, int crossP1, int crossP2) {
			Board child = new Board();

			// Take parent1 (current board) up to point 1
			for (int i = 0; i < crossP1; i++) {
				child.vector[i] = this.vector[i];
			}
			// Take parent 2 from point 1 to point 2
			for (int j = crossP1; j < crossP2; j++) {
				child.vector[j] = board.vector[j];
			}
			// Take parent 1 (current board) from point 2 to end 
			for (int j = crossP2; j < BOARD_SIZE; j++) {
				child.vector[j] = this.vector[j];
			}
			return child.vector();
		}

		/*
		 * 	Randomly takes a bit from the vector and chooses a new random value
		 * 	to put in its place. 
		 *	Random probability is denoted by the mutation rate
		 */
		public void mutateBoard() {

        	for (int j = 0; j < BOARD_SIZE; j++) {
            	if (Math.random() < MUTATION_RATE) {
            		this.vector[j] = 1 + (int)(Math.random() * (8));
            	}
        	}
		}

		/*
		 * 	Method to set the vector of a board	
		 */
		public void vector(int[] vector) {
			this.vector = vector;
		}

		/*
		 * 	Method to retrieve the vector of a board	
		 */
		public int[] vector() {
			return this.vector;
		}

		/*
		 * 	Method to retrieve the cost of a board	
		 */
		public int cost() {
			return cost;
		}
	}
}

