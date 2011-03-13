package net.redstonebat.applet;

public class Lever extends Blocks
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 997859963700728969L;
	private String id = "switch";
	
	@Override
	public void calculate(Blocks caller, Grid g)
	{
		calculateAll(g);
	}

	@Override
	public String getFileName(Grid g)
	{
		return ((getOrient()==BELOW)?"standing":"hanging") + id + "-" + (getState()?"on":"off");
	}

	@Override
	public int getRotation(Grid g)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
