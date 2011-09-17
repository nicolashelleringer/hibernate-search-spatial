package net.novacodex.hibernate.search.spatial;

final class Point implements SpatialIndexable {

	private final double latitudeDeg;
	private final double latitudeRad;
	private final double longitudeDeg;
	private final double longitudeRad;

	/**
	 * @param latitude  in degrees
	 * @param longitude in degrees
	 * @return a point with coordinates given in degrees
	 */
	public static Point fromDegrees( double latitude, double longitude ) {
		// Normalize longitude in [-180;180]
		longitude = ( ( longitude + ( GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 ) ) % GeometricConstants.LONGITUDE_DEGREE_RANGE ) - ( GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 );

		if ( latitude > GeometricConstants.LATITUDE_DEGREE_MAX || latitude < GeometricConstants.LATITUDE_DEGREE_MIN ) {
			throw new IllegalArgumentException( "Illegal latitude value for Point creation" );
		}

		return new Point( latitude, longitude );
	}

	/**
	 * @param latitude  in radians
	 * @param longitude in radians
	 * @return a point with coordinates given in radians
	 */
	public static Point fromRadians( double latitude, double longitude ) {
		return fromDegrees( Math.toDegrees( latitude ), Math.toDegrees( longitude ) );
	}

	/**
	 * @param latitude  in degrees
	 * @param longitude in degrees
	 */
	private Point( double latitude, double longitude ) {
		this.latitudeDeg = latitude;
		this.longitudeDeg = longitude;
		this.latitudeRad = Math.toRadians( latitude );
		this.longitudeRad = Math.toRadians( longitude );
	}

	public Point computeDestination( double distance, double heading ) {
		double headingRadian = Math.toRadians( heading );

		// Haversine formula (http://www.movable-type.co.uk/scripts/latlong.html)
		double destinationLatitudeRadian = Math.asin( Math.sin( latitudeRad ) * Math.cos( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) + Math.cos( latitudeRad ) * Math.sin( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) * Math.cos( headingRadian ) );

		double destinationLongitudeRadian = longitudeRad + Math.atan2( Math.sin( headingRadian ) * Math.sin( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) * Math.cos( latitudeRad ), Math.cos( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) - Math.sin( latitudeRad ) * Math.sin( destinationLatitudeRadian ) );

		return fromRadians( destinationLatitudeRadian, destinationLongitudeRadian );
	}

	public double getDistanceTo( Point other ) {
		// Haversine formula (http://www.movable-type.co.uk/scripts/latlong.html)
		return Math.acos( Math.sin( latitudeRad ) * Math.sin( other.latitudeRad ) + Math.cos( latitudeRad ) * Math.cos( other.latitudeRad ) * Math.cos( other.longitudeRad - longitudeRad ) ) * GeometricConstants.EARTH_MEAN_RADIUS_KM;
	}

	@Override
	public double getLatitude() {
		return latitudeDeg;
	}

	public double getLatitudeRad() {
		return latitudeRad;
	}

	@Override
	public double getLongitude() {
		return longitudeDeg;
	}

	public double getLongitudeRad() {
		return longitudeRad;
	}
}