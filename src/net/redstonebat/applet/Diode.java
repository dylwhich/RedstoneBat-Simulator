package net.redstonebat.applet;

public class Diode extends Blocks
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3668905961382577808L;
	public String id = "diode";

	@Override
	public void calculate(Blocks caller,Grid g)
	{
		boolean oldState = getState();
		setState(getSurroundings(g)[getOrient()].getState());
		if (oldState != getState()) g.addUpdate(getPos());
	}

	@Override
	public String getFileName(Grid g)
	{
		return id + "-" + (getState()?"on":"off");
	}

	@Override
	public int getRotation(Grid g)
	{
		return getOrient();
	}

}
