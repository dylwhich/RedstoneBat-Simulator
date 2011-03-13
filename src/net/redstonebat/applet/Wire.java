package net.redstonebat.applet;

public class Wire extends Blocks
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3724068396864912251L;
	//private static String name = "Torch";
	private static String id = "wire";
	private String type;
	private int rotation = 0;
	
	public String getFileName(Grid g)
	{
		calcRotation(g);
		return type + id + "-" + (getState()?"on":"off");
	}
	
	public void calculate(Blocks caller, Grid g)
	{
		for (Blocks c : this.getSurroundings(g))
		{
			if (c != null)
			{
				if (c.getState()==true)
				{
					if (c.getCharge()-1>this.getCharge()) setCharge(c.getCharge()-1);
					if (getCharge() > 0) setState(true);
				}
			}
		}
		
		//if (oldRot != rotation || oldCharge != getCharge() || oldState != getState())
			//g.addUpdate(getPos());
		
		calculateAll(g);
	}

	@Override
	public int getRotation(Grid g)
	{
		calcRotation(g);
		return rotation;
	}
	
	
	private void calcRotation(Grid g)
	{
			//for some of these, "on" actually represents the attached-ness... 
		Blocks[] surrs = getSurroundings(g);
		boolean attached[] = new boolean[4];
		int numOn = 0;
		boolean consec = false;
		int firstOn = -1;
		int lastOn = 0;
		for (int i=0;i<4;i++)	//ignore above and below
		{
			int n = (i+1)%4;
			Blocks curr = surrs[i];
			boolean attaches = (curr instanceof Wire) || (curr instanceof Torch);
			boolean nAttaches = (surrs[n] instanceof Wire || surrs[n] instanceof Torch);
			if (attaches)
			{
				numOn++;
				attached[i] = true;
				if (firstOn == -1 && nAttaches) firstOn = i;
				if (nAttaches) consec = true;
				else lastOn = i;
			}
		}
		
		switch(numOn) {
		case 1:
		case 2:
			if (consec)
			{
				System.out.printf("Block at %s: firstOn: %d lastOn: %d%n",getPos(),firstOn,lastOn);
				type = "corner";
				rotation = firstOn-1;
			} else
			{
				rotation = lastOn;
				type = "line";
			}
		break;
		
		case 3:
			if (!attached[firstOn+1]) rotation = lastOn-1;
			else rotation = firstOn+1;
			rotation = firstOn;
			type = "t";
		break;
	
		case 4:
		case 0:
			rotation = 0;
			type = "cross";
		break;
		
		default:
			type = "cross";
			System.out.println("What?? numOn is " + numOn);
		break;
		}
	}
}
