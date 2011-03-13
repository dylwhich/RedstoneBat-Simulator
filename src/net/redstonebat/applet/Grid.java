package net.redstonebat.applet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @version 0.7 (File porsing support)
 */


public class Grid
{

	
	/*
	 * *-X----------->
	 * |\ 
	 * Y Z
	 * |  \
	 * |   \
	 * |    \
	 * v
	 * 
	 * 
	 */
	private Blocks[][][] grid;	//lolwut
	private ArrayList<Point3D> updatedBlocks = new ArrayList<Point3D>();
	
	private HashMap<String,Point3D> inputs = new HashMap<String,Point3D>();
	private HashMap<String,Point3D> outputs = new HashMap<String,Point3D>();
	
	public static Grid fromFile(File f) throws IOException, SAXException
	{
		XMLReader xr = XMLReaderFactory.createXMLReader();
		RSBParser handler = new RSBParser();
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);	//TODO: figure out what to actually do here
		
		FileReader r = new FileReader(f);
		xr.parse(new InputSource(r));
		
		return handler.getGrid();
	}
	
	/*public Grid(Grid g, HashMap<String,Point3D> in, HashMap<String,Point3D> out)
	{
		this(g.width(),g.height(),g.width());
		
	}*/
	
	public Grid()
	{
		this(10);
	}

	public Grid(int size)
	{
		this(size,size);
	}
	
	public Grid(int x, int y)
	{
		this(x,y,1);
	}
	
	public Grid(int x, int y, int z)
	{
		grid = new Blocks[z][y][x];
		
		for (int level=0;level<depth();level++)
		{
			for (int row=0;row<height();row++)
			{
				for (int col=0;col<width();col++)//grid[i][j] = new Air();
				{
						//this is starting to get unwieldy...
						//good thing I'll never need any more dimensions *knocks on wood and runs away from Einstein*
					set(col,row,level,new Air());
				}
			}
		}
	}
	
	public Grid(Grid g)
	{
		//do nothing
	}
	
	public Blocks get(Point3D p)
	{
		return get(p.x,p.y,p.z);
	}
	
	public Blocks get(int x, int y, int z)
	{
		return grid[z][y][x];
	}
	
	public void set(int x, int y, int z, Blocks b)
	{
		b.setPos(new Point3D(x,y,z));
		grid[z][y][x] = b;
	}
	
	public void set(Point3D p, Blocks b)
	{
		set(p.x,p.y,p.z,b);
	}
	
	public void replace(Blocks a, Blocks b)
	{
		replace(a.getPos(),b);
	}
	
	public void replace(Point3D p, Blocks b)
	{
		replace(p.x,p.y,p.z,b);
	}
	
	public void replace(int x, int y, int z, Blocks b)
	{
		set(x,y,z,b);
	}
	
	public Blocks[] getSurroundings(Blocks b)
	{
		return getSurroundings(b.getPos());
	}
	
	public Blocks[] getSurroundings(Point3D p)
	{
		return getSurroundings(p.x,p.y,p.z);
	}
	
	public Blocks[] getSurroundings(int x,int y,int z)
	{
			//if cardinal constants change, this could break. best to fix it now
		//Blocks[] surrs = {safeGet(x,y,z,Blocks.NORTH),safeGet(x,y,z,Blocks.EAST),safeGet(x,y,z,Blocks.SOUTH),safeGet(x,y,z,Blocks.WEST),safeGet(x,y,z,Blocks.ABOVE),safeGet(x,y,z,Blocks.BELOW)};
		Blocks[] surrs = new Blocks[6];
		
		surrs[Blocks.NORTH] = safeGet(x,y,z,Blocks.NORTH);
		surrs[Blocks.EAST] = safeGet(x,y,z,Blocks.EAST);
		surrs[Blocks.SOUTH] = safeGet(x,y,z,Blocks.SOUTH);
		surrs[Blocks.WEST] = safeGet(x,y,z,Blocks.WEST);
		surrs[Blocks.ABOVE] = safeGet(x,y,z,Blocks.ABOVE);
		surrs[Blocks.BELOW] = safeGet(x,y,z,Blocks.BELOW);
		
		return surrs;
	}
	
	public int depth()	//Z
	{
		return grid.length;
	}
	
	public int height()	//Y
	{
		return grid[0].length;
	}
	
	public int width()	//X
	{
		return grid[0][0].length;
	}
	
	public Blocks safeGet(Point3D p, int dir)
	{
		return safeGet(p.x,p.y,p.z,dir);
	}
	
	public Blocks safeGet(int x, int y, int z, int dir)
	{
		switch(dir)
		{
			case Blocks.NORTH:
				y++;
			break;
			case Blocks.SOUTH:
				y--;
			break;
			case Blocks.EAST:
				x++;
			break;
			case Blocks.WEST:
				x--;
			break;
			case Blocks.ABOVE:
				z++;
			break;
			case Blocks.BELOW:
				z--;
			break;
		}
		return inBounds(x,y,z)?get(x,y,z):new Air(x,y,z);		//protip: creating air without an initial position lead to some nasty null pointer exceptions
	}
	
	public boolean inBounds(Point3D p)
	{
		return inBounds(p.x,p.y,p.z);
	}
	
	public boolean inBounds(int x, int y, int z)
	{
		return x<width()&&x>=0&&y<height()&&y>=0&&z<depth()&&z>=0;	//ahhhhhhh
	}
	
		//JAVA, YOU ARE NOT SGML. WHY DO YOU HAVE SO MANY ANGLE BRACKETS. WHYYYYYYYYYYYYYY
	public HashMap<String,Point3D> getInputs()
	{
		return new HashMap<String,Point3D>(inputs);
	}
	
	public void setInputs(HashMap<String,Point3D> in)
	{
		inputs = new HashMap<String,Point3D>(in);
	}
	
	public HashMap<String,Point3D> getOutputs()
	{
		return new HashMap<String,Point3D>(outputs);
	}
	
	public void setOutputs(HashMap<String,Point3D> out)
	{
		outputs = new HashMap<String,Point3D>(out);
	}
	
	public void resetCalcs()
	{
		for (int level=0;level<depth();level++) for (int row=0;row<height();row++) for (int col=0;col<width();col++)//grid[i][j] = new Air();
			get(col,row,level).setCalced(false);
	}
	
	@Override
	public String toString()
	{
		String buff = "";
		for (int i=0;i<grid.length;i++)
		{
			for (int j=0;j<grid[i].length;j++)
			{
				//buff += String.format("%s/%s.png",Block.symbols[grid[i][j].getType()]);
			}
			buff += "\n";
		}
		return buff;
	}
	
		//These names are kind of misleading... perhaps change to queueUpdate, which is equally misleading?
	public void addUpdate(Point3D p)
	{
		if (!updatedBlocks.contains(p)) updatedBlocks.add(p);
	}
	
	public void clearUpdates()
	{
		updatedBlocks.clear();
	}
	
	public boolean isUpdated(Point3D p)
	{
		return updatedBlocks.contains(p);
	}
	
	public boolean hasNextUpdate()
	{
		return updatedBlocks.size() != 0;
	}
	
	public Point3D nextUpdate()
	{
		Point3D p = updatedBlocks.get(0);
		updatedBlocks.remove(0);
		return p;
	}
}
