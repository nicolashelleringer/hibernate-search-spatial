package net.novacodex.hibernate.search.spatial;

/**
 * @author Nicolas Helleringer
 * @author Mathieu Perez
 *         <p/>
 *         Geometric constants to use in GridHelper calculation
 * @see GridHelper
 */
interface GeometricConstants {

	int LONGITUDE_DEGREE_RANGE = 360;
	int LONGITUDE_DEGREE_MIN = -LONGITUDE_DEGREE_RANGE / 2;
	int LONGITUDE_DEGREE_MAX = LONGITUDE_DEGREE_RANGE / 2;
	int LATITUDE_DEGREE_RANGE = 180;
	int LATITUDE_DEGREE_MIN = -LATITUDE_DEGREE_RANGE / 2;
	int LATITUDE_DEGREE_MAX = LATITUDE_DEGREE_RANGE / 2;
	int HEADING_NORTH = 0;
	int HEADING_SOUTH = 180;
	int HEADING_EAST = 90;
	int HEADING_WEST = 270;
	double EARTH_MEAN_RADIUS_KM = 6371.0;
	double EARTH_EQUATOR_CIRCUMFERENCE_KM = 40075.017;
	double PROJECTED_LATITUDE_RANGE = Math.PI;
	double PROJECTED_LONGITUDE_RANGE = 2 * Math.PI;
	Point NORTH_POLE = Point.fromDegrees( LATITUDE_DEGREE_MAX, 0 );
	Point SOUTH_POLE = Point.fromDegrees( LATITUDE_DEGREE_MIN, 0 );
}