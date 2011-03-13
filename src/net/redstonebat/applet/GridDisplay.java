package net.redstonebat.applet;

import java.applet.Applet;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.xml.sax.SAXException;

public class GridDisplay extends Applet
{
	private static final long serialVersionUID = 267410565159739912L;
	
	private static final int IMG_SPACING = 0;
	private static final int IMG_SIZE = 16;
	private static final int GRID_LEFT_MARGIN = 4;
	private static final int GRID_TOP_MARGIN = 36;
	
	private Grid grid;
	private int currLvl = 0;
	private Blocks focus;
	private Point3D focusPos;
	
	private Button incLev, decLev;
	
		//given the location of the mouse on screen, returns the location of a block, or null if outside of the range
	public Point translatePos(int x, int y)
	{
		Point p = new Point(x,y);
		
		p.x -= GRID_LEFT_MARGIN;
		p.y -= GRID_TOP_MARGIN;
		
		p.x /= (IMG_SIZE + IMG_SPACING);
		p.y /= (IMG_SIZE + IMG_SPACING);
		
		if (p.x < 0 || p.x > grid.width()-1 || p.y < 0 || p.y > grid.height()-1) return null;
		else return p;
	}
	
	public void recalc()
	{
		HashMap<String,Point3D> inputs = grid.getInputs();
		Collection<Point3D> inputPoints = inputs.values();
		
		for (Point3D p : inputPoints)
		{
			grid.get(p).calculate(grid);
			grid.resetCalcs();
		}
			//FFFFFFFUUUUUUUUUUUUUUU
		//grid.get(inputs.get(inputs.keySet().toArray()[0])).calculate(grid);
	}
	
		//calculates all the inputs, in addition to the given point... necessary to make things display properly.
	public void recalc(Point3D p)
	{
		recalc();
		grid.get(p).calculate(grid);
		grid.resetCalcs();
	}
	
	public void init()
	{
		incLev = new Button("Level Up");
		decLev = new Button("Level Down");
			//this makes them kind of backwards but it doesn't feel right the other way
		add(incLev);//,0,(grid.height()+4)*(IMG_SPACING + IMG_SIZE));
		add(decLev);//,0,(grid.height()+7)*(IMG_SPACING + IMG_SIZE));
		//setSize((grid.width()+2)*(IMG_SPACING + IMG_SIZE) + GRID_LEFT_MARGIN,(grid.height()+2)*(IMG_SPACING + IMG_SIZE) + GRID_TOP_MARGIN);*/
		setSize(300,300);
		
		try
		{
			grid = Grid.fromFile(new File("../etc/wire_test.rsb"));
			//grid = new Grid(15,15,3);
			
		} catch (SAXException e)
		{
			e.printStackTrace();
			
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch(Exception e)
		{
			System.out.println("Error: Malformed XML. Could not load grid.");
			return;
		}
		
			//make sure they all update at least once
		for (int x=0;x<grid.width();x++)
		{
			for (int y=0;y<grid.height();y++)
			{
				for (int z=0;z<grid.depth();z++)
				{
					grid.addUpdate(new Point3D(x,y,z));
				}
			}
		}
		repaintAll();
		
		recalc();
	}
	
	@Override
	public boolean mouseUp(Event e, int x, int y)
	{
		//System.out.printf("mouseUp@%d,%d -- Block: %s%n",x,y,translatePos(x,y));
		Point pos = translatePos(x,y);
		if (pos != null)
		{
			focusPos = new Point3D(pos.x,pos.y,currLvl);
			focus = grid.get(focusPos);
			
			if ((e.modifiers & 4) == 4)	//right click
			{
				if (focus.isMutable()) grid.set(focusPos,new Air());
			}
			else if ((e.modifiers & 8) == 8)	//middle click
			{
				if (grid.getInputs().containsValue(focusPos)) focus.setState(!focus.getState());
			}
			else if (e.modifiers == 0)
			{
				if (focus.isMutable()) grid.set(focusPos, new Wire());
			}
			
			recalc(focusPos);

			grid.addUpdate(focusPos);
			//repaint(0);
			//recalc();
			repaint(0);
		}
		
		return true;
	}
	
	@Override
	public boolean action(Event e, Object args)
	{
		int d = currLvl;
		if (e.target == incLev && currLvl < grid.depth()-1) currLvl++;
		else if (e.target == decLev && currLvl > 0) currLvl--;
		if (currLvl != d) repaint();
		return true;
	}
	
	public void repaintAll()
	{
		repaint(0,0,200,200);
	}
	
	@Override
	public void repaint()
	{
		while (grid.hasNextUpdate())
		{
			Point p = grid.nextUpdate();
			repaint(GRID_LEFT_MARGIN + (IMG_SIZE + IMG_SPACING) * p.x,GRID_TOP_MARGIN + (IMG_SIZE + IMG_SPACING) * p.y,IMG_SIZE,IMG_SIZE);
		}
		grid.clearUpdates();
	}
	
	@Override
	public void update(Graphics g)
	{
		paint(g);
	}
	
	@Override
	public void paint(Graphics g)
	{
		//g.setXORMode(new Color(0,255,0));
		//g.setColor(new Color(0,255,0));
        //for (int i=0;i<grid.width();i++)
        
		//{
        	//for (int j=0;j<grid.height();j++)
        	while (grid.hasNextUpdate())
			{
        		Blocks currBlock = grid.get(grid.nextUpdate());
        		int i = currBlock.getPos().x;
        		int j = currBlock.getPos().y;
        		//Blocks currBlock = grid.get(i,j,currLvl);
        		
        		BufferedImage img = null;
				try
				{
					img = ImageIO.read(new File(Blocks.IMG_PATH + currBlock.getFileName(grid) + ".png"));
					
						//Transform the image to face the proper direction
					AffineTransform transform = new AffineTransform();
					double rotation = currBlock.getRotation(grid) * (Math.PI/2.0);
					transform.rotate(rotation,img.getWidth()/2,img.getHeight()/2);
					AffineTransformOp op = new AffineTransformOp(transform,AffineTransformOp.TYPE_BILINEAR);
					img = op.filter(img,null);
					
					
						//will allow drawing things on top of blocks in the current level.
						//does not work well without transparency :(
					/*if (currBlock instanceof Block && !(currBlock.getSurroundings()[Blocks.ABOVE] instanceof Air))
					{
						img = ImageIO.read(new File(Blocks.IMG_PATH + currBlock.getSurroundings()[Blocks.ABOVE].getFileName() + ".png"));
		        		g.drawImage(img,(IMG_SPACING + IMG_SIZE)*i + GRID_LEFT_MARGIN,(IMG_SPACING + IMG_SIZE)*j + GRID_TOP_MARGIN,this);
					}*/
					
	        		g.drawImage(img,(IMG_SPACING + IMG_SIZE)*i + GRID_LEFT_MARGIN,(IMG_SPACING + IMG_SIZE)*j + GRID_TOP_MARGIN,this);
	        		
	        		
				} catch (IOException e)
				{
					e.printStackTrace();
				}
        	}
        //}
        g.drawString("Current level: " + currLvl,0,(grid.height()+1)*(IMG_SPACING + IMG_SIZE) + GRID_TOP_MARGIN);
	}

}
