package net.redstonebat.applet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Blocks implements Serializable
{	
	
		//can be replaced with cleaner 'instanceof'
	/*public static final int AIR = 0,
		TORCH = 1,
		WIRE = 2,
		DIODE = 3,
		BLOCK = 4,
		OUTPUT = 5,
		INPUT = 6,
		SIGN = 7;*/
	
	private static final long serialVersionUID = -2724824949659659717L;
	
	public static final String IMG_PATH = "../img/";
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, ABOVE = 4, BELOW = 5;
	public static final int CHARGE_OFF = 0, CHARGE_SOURCE = 16;	//actually, 1-16 is on, but 16 is source
	
	@SuppressWarnings("unused")
	private static String id;	//for filename and such
	
	private String comment;	//used like a comment... like this.
	
	private boolean state;	//made redundant by charge?
	private int orient = BELOW;		//Changed from ABOVE because it makes more sense, in that a sitting block will be in the lower portion of the block it occupies. Also, it lets me get rid of a single line of code.
	private boolean mutable = true;	//whether or not a user can change this
	
	private Point3D pos;
	private boolean calced;
	private int charge;// = CHARGE_OFF;	//16 = on, 0 = off -- should be zero already anyway
	
	public Blocks()
	{
		this(0,0,0);
	}
	
	public Blocks(Point3D p)
	{
		setPos(p.x,p.y,p.z);
	}
	
	public Blocks(int x, int y, int z)
	{
		setPos(x,y,z);
	}
	
	public int getCharge()
	{
		return charge;
	}
	
	public void setCharge()	//should only be used for torches or not at all
	{
		setCharge(getState()?CHARGE_SOURCE:CHARGE_OFF);
	}
	
	public void setCharge(int c)
	{
		validateCharge(c);
		charge = c;
	}
	
	private void validateCharge(int c)
	{
		if (c > CHARGE_SOURCE) throw new IllegalArgumentException("Charge must be in range 0-16.");
	}
	
	public static void wait(int ticks)
	{
		try
		{
			Thread.currentThread();
			Thread.sleep(200);
		} catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean getState()
	{
		return state;
	}
	
	public void setState(boolean b)
	{
		state = b;
	}
	
	public int getOrient()
	{
		return orient;
	}
	
	public void setOrient(int o)
	{
		validateOrient(o);
		orient = o;
	}
	
	private void validateOrient(int or)
	{
		if (or < NORTH || getOrient() > BELOW) throw new IllegalArgumentException(orient + " is not a valid orientation.");
	}
	
	public boolean isMutable()
	{
		return mutable;
	}
	
	public void setMutable(boolean m)
	{
		mutable = m;
	}
	
	public Point3D getPos()
	{
		return new Point3D(pos);	//this is not a setter!
	}
	
	public void setPos(Point3D p)
	{
		validatePos(p);
		pos=new Point3D(p);
	}
	
	public void setPos(int x, int y, int z)
	{
		setPos(new Point3D(x,y,z));
	}
	
	public void validatePos(Point3D p)
	{
			//it doesn't complain until they're out by more than one because fake blocks are used when an element is at the edge of an axes
		if (p.x<-1 || p.y<-1 || p.z<-1) throw new IllegalArgumentException("Invalid position");
	}
	
	public void setComment(String c)
	{
		comment = c;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public Blocks[] getSurroundings(Grid g)
	{
		return g.getSurroundings(this);
	}
	
	/**
	 * The name for this block's image file in its current state -- WITHOUT ".png" or a leading "/"
	 * @return The file name
	 */
	public abstract String getFileName(Grid g);
	
	/**
	 * Re-calculate the state of the blocks and trigger an update of adjacent blocks, if necessary.
	 * @param The block that called this method
	 * @param The grid with which to calculate
	 */
	public abstract void calculate(Blocks caller, Grid g);
	
	/**
	 * Calculates the number of 90° clockwise rotations this block's image should have to properly reflect its state  
	 * @return the number of quarter-turns
	 */
	public abstract int getRotation(Grid g);
	
	public boolean isCalced()
	{
		return calced;
	}
	
	public void setCalced(boolean c)
	{
		calced = c;
	}
	
	public void calculate(Grid g)
	{
		calculate(this,g);
	}

	private void validateState()	//oh man it would be bad if the actual state needed validation
	{
		validateCharge(charge);
		validateOrient(orient);
		validatePos(pos);
	}
	
	@Override
	public String toString()
	{
		return String.format("Block %s%n\tOn: %b, Orient: %d, Mutable: %b, Pos: %s, Calced: %b, Charge: %d%n",getClass().getName(),state,orient,mutable,pos,calced,charge);
	}
	
	public void calculateAll(Grid g)
	{
		g.addUpdate(getPos());
		setCalced(true);
		for (Blocks c : getSurroundings(g))
		{
			if (c!=null && !(c instanceof Air) && !c.isCalced()) c.calculate(this,g);
		}
	}
	
	/**
	* Always treat de-serialization as a full-blown constructor, by
	* validating the final state of the de-serialized object.
	*/
	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
	{
		//always perform the default de-serialization first
		aInputStream.defaultReadObject();
	
		//make defensive copy of the mutable pos field
		pos = new Point3D(pos);
		
		//ensure that object state has not been corrupted or tampered with maliciously
		validateState();
	}
	
	/**
	* This is the default implementation of writeObject.
	* Customize if necessary.
	*/
	private void writeObject(ObjectOutputStream aOutputStream) throws IOException
	{
		//perform the default serialization for all non-transient, non-static fields
		aOutputStream.defaultWriteObject();
	}
}