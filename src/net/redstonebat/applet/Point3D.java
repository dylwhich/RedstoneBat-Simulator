package net.redstonebat.applet;

import java.awt.Point;

public class Point3D extends Point {

	/**
	 * Seriously, why does java not already have one of these?
	 */
	
	private static final long serialVersionUID = 3578330675190879612L;
	
	public int z;
	
	public Point3D(Point3D p)
	{
		this(p.x,p.y,p.z);
	}
	
	public Point3D(int x, int y, int z)
	{
		super(x,y);
		this.z=z;
	}
	
	public double getZ()
	{
		return (double)z;
	}
	
	public boolean equals(Point3D p)
	{
		return this.x==p.x&&this.y==p.y&&this.z==p.z;
	}
	
	public String toString()
	{
		return String.format("(%d,%d,%d)",x,y,z);
	}
}
