package net.novacodex.hibernate.search.spatial;

public class Point implements SpatialIndexable {

	private final double latitude;
	private final double latitudeRad;
	private final double longitude;
	private final double longitudeRad;

	/**
	 * @param latitude in degrees
	 * @param longitude in degrees
	 */
	public Point( double latitude, double longitude ) {
		//Normalize in [-180;180]
		this.longitude = ( ( longitude + ( GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 ) ) % GeometricConstants.LONGITUDE_DEGREE_RANGE ) - ( GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 );
		this.longitudeRad = Math.toRadians( longitude );

		if ( latitude > GeometricConstants.LATITUDE_DEGREE_MAX || latitude < GeometricConstants.LATITUDE_DEGREE_MIN ) {
			throw new IllegalArgumentException( "Illegal latitude value for Point creation" );
		}

		this.latitude = latitude;
		this.latitudeRad = Math.toRadians( latitude );
	}

	public Point computeDestination( double distance, double heading ) {
		double headingRadian = Math.toRadians( heading );

		// Haversine formula (http://www.movable-type.co.uk/scripts/latlong.html)
		double destinationLatitudeRadian = Math.asin( Math.sin( latitudeRad ) * Math.cos( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) + Math.cos( latitudeRad ) * Math.sin( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) * Math.cos( headingRadian ) );

		double destinationLongitudeRadian = longitudeRad + Math.atan2( Math.sin( headingRadian ) * Math.sin( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) * Math.cos( latitudeRad ), Math.cos( distance / GeometricConstants.EARTH_MEAN_RADIUS_KM ) - Math.sin( latitudeRad ) * Math.sin( destinationLatitudeRadian ) );

		return new Point( Math.toDegrees( destinationLatitudeRadian ), Math.toDegrees( destinationLongitudeRadian ) );
	}

	public double getDistanceTo( Point other ) {
		// Haversine formula (http://www.movable-type.co.uk/scripts/latlong.html)
		return Math.acos( Math.sin( latitudeRad ) * Math.sin( other.latitudeRad ) + Math.cos( latitudeRad ) * Math.cos( other.latitudeRad  ) * Math.cos( other.longitudeRad - longitudeRad ) ) * GeometricConstants.EARTH_MEAN_RADIUS_KM;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	public double getLatitudeRad() {
		return latitudeRad;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	public double getLongitudeRad() {
		return longitudeRad;
	}
}
