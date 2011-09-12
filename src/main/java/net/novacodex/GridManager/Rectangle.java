package net.novacodex.GridManager;

public class Rectangle {
	private Point lowerLeft;
	private Point upperRight;

	public void setUpperRight( Point upperRight ) {
		this.upperRight = upperRight;
	}

	public void setLowerLeft( Point lowerLeft ) {
		this.lowerLeft = lowerLeft;
	}

	public Point getLowerLeft() {
		return lowerLeft;
	}

	public Point getUpperRight() {
		return upperRight;
	}

	public Rectangle() {
		this.setLowerLeft(new Point());
		this.setUpperRight(new Point());
	}

	public Rectangle(Point lowerLeft, Point upperRight) {
		this.setLowerLeft(lowerLeft);
		this.setUpperRight(upperRight);
	}

	public Rectangle(Rectangle rectangle) {
		this.setLowerLeft(rectangle.getLowerLeft());
		this.setUpperRight(rectangle.getUpperRight());
	}
}
