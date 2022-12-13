package Tenta160234.uppgift3;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Maze m = new Maze();
		m.print();
		System.out.println();
		if(m.solve())
			System.out.println("Labyrinten har en utgang");
		else
			System.out.println("Labyrinten saknar utgang");
		System.out.println();
		m.print();

	}
	

}