package net.redstonebat.applet;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class RSBParser extends DefaultHandler
{
	private Grid g;			//constructed piece by piece from the datas
	@SuppressWarnings("unused")
	private boolean inFill;	//TODO: NYI
	private boolean inTag;
	private Point3D cursor;	//the current 
	
	private HashMap<String,Point3D> inputs = new HashMap<String,Point3D>();
	private HashMap<String,Point3D> outputs = new HashMap<String,Point3D>();
	
	private enum Tag	//I like my switch, foo!
	{
		AIR, BLOCK, DIODE, LEVER, TORCH, WIRE,		//Blocks
		GRID, LEVEL, ROW,		//Hierarchical Tags
		FILL,		//Special function tags
		INVALID;		//Duhhh
		public static Tag from(String str)
		{
			try
			{
				return valueOf(str.toUpperCase());
			} catch(Exception e)
			{
				return INVALID;		//see 17
			}
		}
	}
	
	private enum Attribute	//TODO dat enum. implement et.
	{
		SIZE,	//Won't really be used, but it's here for completeness' sake
		INPUT, OUTPUT,	//When present, adds the current block to the respective list of inputs/outputs
		ORIENTATION, STATE, MUTABLE, CHARGE,	//Standard attributes, simply set the block's attribute of the same name
		INVALID;		//had best not be happening
		public static Attribute from(String str)
		{
			try
			{
				return valueOf(str.toUpperCase());
			} catch(Exception e)
			{
				return INVALID;		//see 17
			}
		}
	}
	
	private enum Orient		//This should probably be in Blocks
	{
		NORTH(0), EAST(1), SOUTH(2), WEST(3), ABOVE(4), BELOW(5), INVALID(6);
		
		private int val;
		
		private Orient(int v)
		{
			val = v;
		}
		
		public int toInt()
		{
			return val;
		}
		
		public static Orient from(String str)
		{
			try
			{
				return valueOf(str.toUpperCase());
			} catch(Exception e)
			{
				return INVALID;		//see 17
			}
		}
	}
	
	public RSBParser()
	{
		super();
		cursor = new Point3D(0,0,0);	//It's a good place to start off.
	}
	
	@Override
	public void startDocument()
	{
		//do nothing
	}
	
	@Override
	public void endDocument()
	{
		//also do nothing
	}
	
	@Override
	public void startElement(String uri, String name, String qName, Attributes atts)
	{
		inTag = true;
		Blocks b = null;	//temporary variable, cleans up the switch a bit imo
		
		switch(Tag.from(name)) {	//bloody switch conventions
		case GRID:
			for (int i=0;i<atts.getLength();i++)
			{
				if (atts.getLocalName(i)=="size")
				{
					String[] dims = atts.getValue(i).split(" ",3);
					//String[] dims = atts.getValue(i).split("x",3);
					try
					{
						g = new Grid(Integer.valueOf(dims[0]),Integer.valueOf(dims[1]),Integer.valueOf(dims[2]));
					
					} catch (ArrayIndexOutOfBoundsException e)
					{
						System.out.println("Invalid Grid size!");
						return;
					}
				}
			}
		break;
		
			//Create new block, parse its tag attributes and set them accordingly, and place it in the grid
		case AIR:
			b = new Air();
		break;
		case BLOCK:
			b = new Block();
		break;
		case TORCH:
			b = new Torch();
		break;
		case WIRE:
			b = new Wire();
		break;
		case LEVER:
			b = new Lever();
		break;
		case DIODE:
			b = new Diode();
		break;
		default:
			b = new Air();
		break;
		}
		if (b !=null)	//bloody things.
		{
			setAtts(b,atts);
			g.set(cursor,b);
		}
	}
	
	@Override
    public void endElement (String uri, String name, String qName)
    {
		switch(Tag.from(name)) {
		case LEVEL:
			cursor.x=0;
			cursor.y=0;
			cursor.z++;
		break;
		
		case ROW:
			cursor.x=0;
			cursor.y++;
		break;
		default:
			cursor.x++;
		break;
		}
		
		inTag = false;
	}
	
	@Override
	public void characters (char[] ch, int start, int length)
	{
		if (inTag)
		{
			String comm;
			char[] chars = new char[ch.length];
			System.arraycopy(ch,start,chars,0,length);
			comm = new String(chars);
			g.get(cursor).setComment(comm);
		}
	}
	
	private void setAtts(Blocks b,Attributes atts)	//perhaps use within Blocks method?
	{
		for (int i=0;i<atts.getLength();i++)
		{
			String name = atts.getLocalName(i);
			String val = atts.getValue(i);
			
			switch(Attribute.from(name)) {
			
			case SIZE:
				// N/A - Handled elsewhere
			break;
			
			case INPUT:
				inputs.put(val,new Point3D(cursor));
				g.setInputs(inputs);
			break;
			
			case OUTPUT:
				outputs.put(val,new Point3D(cursor));
				g.setOutputs(outputs);
			break;
			
			case ORIENTATION:
				b.setOrient(Orient.from(val).toInt());
			break;
			
			case STATE:
				b.setState(val=="on");	//TODO it may be preferable to set this ONLY on "on" and "off", raising an error otherwise
			break;
			
			case MUTABLE:
				b.setMutable(val!="false");
			break;
			
			case CHARGE:
				b.setCharge(Integer.parseInt(val));
			break;
			
			
			//the department of redundancy department will be pleased
			case INVALID:
			default:
				System.out.println("Error: Invalid attribute");
			break;
			}
		}
	}
	
	public Grid getGrid()
	{
		return g;
	}
}
