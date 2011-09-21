package net.novacodex.hibernate.search.spatial;

/**
 * @author Nicolas Helleringer
 *<p>
 *     minimum interface for a field/method to be grid indexable
 */
public interface SpatialIndexable {
	/**
	 * @return the latitude in degrees
	 */
	double getLatitude();

	/**
	 * @return the longitude in degrees
	 */
	double getLongitude();
}