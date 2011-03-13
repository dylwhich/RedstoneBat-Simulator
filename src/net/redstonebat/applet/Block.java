/**
 * 
 */
package net.redstonebat.applet;


public class Block extends Blocks
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5539318535983088718L;
	private static String id = "block";
	
	public String getFileName(Grid g)
	{
		return id;
	}
	
	public void calculate(Blocks caller,Grid g)
	{
		int i=0;	//meh
		for (Blocks b : getSurroundings(g))
		{
				//blocks do not effect other blocks, and blocks above this do not affect it either
			if (!(b instanceof Block) && (i != ABOVE) && b.getState()) setState(true);
			i++;
		}
		
		calculateAll(g);
		//this.setState(surr[NORTH].getState()||surr[EAST].getState()||surr[SOUTH].getState()||surr[WEST].getState()||surr[BELOW].getState());
	}

	@Override
	public int getRotation(Grid g) {
		// TODO Auto-generated method stub
		return 0;
	}
}