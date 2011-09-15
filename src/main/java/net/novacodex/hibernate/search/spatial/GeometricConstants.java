package net.novacodex.hibernate.search.spatial;

public interface GeometricConstants {
	public final static int LONGITUDE_DEGREE_RANGE = 360;
	public final static int LONGITUDE_DEGREE_MIN = -LONGITUDE_DEGREE_RANGE / 2;
	public final static int LONGITUDE_DEGREE_MAX = LONGITUDE_DEGREE_RANGE / 2;
	public final static int LATITUDE_DEGREE_RANGE = 180;
	public final static int LATITUDE_DEGREE_MIN = -LATITUDE_DEGREE_RANGE / 2;
	public final static int LATITUDE_DEGREE_MAX = LATITUDE_DEGREE_RANGE / 2;
	public final static int HEADING_NORTH= 0;
	public final static int HEADING_SOUTH= 180;
	public final static int HEADING_EAST= 90;
	public final static int HEADING_WEST= 270;
	public final static double EARTH_MEAN_RADIUS_KM= 6371.0;
	public final static double EARTH_EQUATOR_CIRCUMFERENCE_KM= 40075.017;
	public final static double PROJECTED_LATITUDE_RANGE= java.lang.Math.PI;
	public final static double PROJECTED_LONGITUDE_RANGE= 2* java.lang.Math.PI;
}


