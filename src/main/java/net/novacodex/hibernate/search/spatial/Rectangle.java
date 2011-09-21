package net.novacodex.hibernate.search.spatial;

/**
 * @author Nicolas Helleringer
 * @author Mathieu Perez
 *         <p/>
 *         Bounding box older for search area on Earth
 */
final class Rectangle {

	private final Point lowerLeft;
	private final Point upperRight;

	public Rectangle(Point lowerLeft, Point upperRight) {
		this.lowerLeft = lowerLeft;
		this.upperRight = upperRight;
	}

	/**
	 * Compute appropriate bouding box on Earth with pole and prime meridian crossing checks
	 *
	 * @param center of the search area
	 * @param radius of the search area
	 *
	 * @return a bouding box for the area
	 *
	 * @see <a href="http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates">Bouding box on Earth calculation</a>
	 */
	public static Rectangle fromBoundingCircle(Point center, double radius) {
		// http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates
		double minimumLatitude, maximumLatitude;
		double minimumLongitude, maximumLongitude;

		if ( radius > center.getDistanceTo( GeometricConstants.NORTH_POLE ) ) {
			maximumLatitude = GeometricConstants.LATITUDE_DEGREE_MAX;
		}
		else {
			maximumLatitude = center.computeDestination( radius, GeometricConstants.HEADING_NORTH ).getLatitude();
		}

		if ( radius > center.getDistanceTo( GeometricConstants.SOUTH_POLE ) ) {
			minimumLatitude = GeometricConstants.LATITUDE_DEGREE_MIN;
		}
		else {
			minimumLatitude = center.computeDestination( radius, GeometricConstants.HEADING_SOUTH ).getLatitude();
		}

		if ( ( radius > 2 * Math.PI * GeometricConstants.EARTH_MEAN_RADIUS_KM * Math.cos(
				Math.toRadians(
						minimumLatitude
				)
		) ) || ( radius > 2 * Math.PI * GeometricConstants.EARTH_MEAN_RADIUS_KM * Math.cos(
				Math.toRadians(
						maximumLatitude
				)
		) ) ) {
			maximumLongitude = GeometricConstants.LONGITUDE_DEGREE_MAX;
			minimumLongitude = GeometricConstants.LONGITUDE_DEGREE_MIN;
		}
		else {
			Point referencePoint = Point.fromDegrees(
					Math.max(
							Math.abs( minimumLatitude ),
							Math.abs( maximumLatitude )
					), center.getLongitude()
			);
			maximumLongitude = referencePoint.computeDestination( radius, GeometricConstants.HEADING_EAST )
					.getLongitude();
			minimumLongitude = referencePoint.computeDestination( radius, GeometricConstants.HEADING_WEST )
					.getLongitude();
		}

		return new Rectangle(
				Point.fromDegrees( minimumLatitude, minimumLongitude ),
				Point.fromDegrees( maximumLatitude, maximumLongitude )
		);
	}

	public Point getLowerLeft() {
		return lowerLeft;
	}

	public Point getUpperRight() {
		return upperRight;
	}

}