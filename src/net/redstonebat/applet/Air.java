package net.redstonebat.applet;


public class Air extends Blocks
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8629844973491744177L;
	private static String id = "air";

	public Air()
	{
		super();
	}
	
	public Air(Point3D p)
	{
		super(p);
	}
	
	public Air(int x, int y, int z)
	{
		this(new Point3D(x,y,z));
	}
	
	@Deprecated
	public Air(Grid g,Point3D p)
	{
		super(p);
	}
	
	@Deprecated
	public Air(Grid g,int x,int y,int z)
	{
		this(g,new Point3D(x,y,z));
	}
	
	public String getFileName(Grid g)
	{
		return id;
	}
	
	public void calculate(Blocks caller, Grid g)
	{
		//calculateAll();	//why would I even do that?
	}

	@Override
	public int getRotation(Grid g) {
		// TODO Auto-generated method stub
		return 0;
	}
}
