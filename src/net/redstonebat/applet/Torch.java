package net.redstonebat.applet;


public class Torch extends Blocks
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6894631366518217648L;
	private static String id = "torch";
	
	
		//Please note: Standing torches now follow the same within-block style as the other orientations. ABOVE is now invalid for torches
	
	public String getFileName(Grid g)
	{
		return ((getOrient()==ABOVE)?"standing":"hanging") + id + "-" + (getState()?"on":"off");
	}
	
	public void calculate(Blocks caller, Grid g)
	{
		Blocks.wait(1);	//accounts for the torch delay -- may not be necessary, depending on realism of problems
		
		setState(!getSurroundings(g)[getOrient()].getState());		//if placed on the side of a block, its orientation is the direction of the block it's sitting on.
		
		setCharge();	//laziness?
		
		//if (oldState != getState() || oldCharge != getCharge())
			//g.addUpdate(getPos());
		
		calculateAll(g);
		//getGrid().addCalced(this.getPos());
	}

	@Override
	public int getRotation(Grid g)
	{
		return getOrient();
	}
}
