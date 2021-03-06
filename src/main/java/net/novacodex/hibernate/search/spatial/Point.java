package net.novacodex.hibernate.search.spatial;

/**
 * @author Nicolas Helleringer
 * @author Mathieu Perez
 *         <p/>
 *         Normalized latitude,longitude holder (in [-90;90],[-180,180]) with distance and destination computations methods
 */
final class Point implements SpatialIndexable {

	private final double latitude;
	private final double longitude;

	/**
	 * @param latitude in degrees
	 * @param longitude in degrees
	 *
	 * @return a point with coordinates given in degrees
	 */
	public static Point fromDegrees(double latitude, double longitude) {
		// Normalize longitude in [-180;180]
		longitude = ( ( longitude + ( GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 ) ) % GeometricConstants.LONGITUDE_DEGREE_RANGE ) - ( GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 );

		if ( latitude > GeometricConstants.LATITUDE_DEGREE_MAX || latitude < GeometricConstants.LATITUDE_DEGREE_MIN ) {
			throw new IllegalArgumentException( "Illegal latitude value for Point creation" );
		}

		return new Point( latitude, longitude );
	}

	/**
	 * @param latitude in radians
	 * @param longitude in radians
	 *
	 * @return a point with coordinates given in radians
	 */
	public static Point fromRadians(double latitude, double longitude) {
		return fromDegrees( Math.toDegrees( latitude ), Math.toDegrees( longitude ) );
	}

	/**
	 * @param latitude in degrees
	 * @param longitude in degrees
	 */
	private Point(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Calculate end of travel point
	 *
	 * @param distance to travel
	 * @param heading of travel
	 *
	 * @return arrival point
	 *
	 * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html">Compute destination</a>
	 */
	public Point computeDestination(double distance, double heading) {
		double headingRadian = Math.toRadians( heading );

		double destinationLatitudeRadian = Math.asin(
				Math.sin( getLatitudeRad() ) * Math.cos( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) + Math.cos(
						getLatitudeRad()
				) * Math.sin( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) * Math.cos(
						headingRadian
				)
		);

		double destinationLongitudeRadian = getLongitudeRad() + Math.atan2(
				Math.sin( headingRadian ) * Math.sin(
						distance / GeometricConstants.EARTH_MEAN_RADIUS_KM
				) * Math.cos( getLatitudeRad() ),
				Math.cos( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) - Math.sin( getLatitudeRad() ) * Math.sin(
						destinationLatitudeRadian
				)
		);

		return fromRadians( destinationLatitudeRadian, destinationLongitudeRadian );
	}

	/**
	 * Compute distance between two points
	 *
	 * @param other
	 *
	 * @return
	 *
	 * @see <a href="http://www.movable-type.co.uk/scripts/latlong.html">Spherical Law of Cosines</a>
	 */
	public double getDistanceTo(Point other) {
		return Math.acos(
				Math.sin( getLatitudeRad() ) * Math.sin( other.getLatitudeRad() ) + Math.cos( getLatitudeRad() ) * Math.cos(
						other.getLatitudeRad()
				) * Math.cos( other.getLongitudeRad() - getLongitudeRad() )
		) * GeometricConstants.EARTH_MEAN_RADIUS_KM;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	public double getLatitudeRad() {
		return Math.toRadians( latitude );
	}

	public double getLongitudeRad() {
		return Math.toRadians( longitude );
	}
}