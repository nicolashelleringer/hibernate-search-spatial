package net.novacodex.hibernate.search.spatial;

import org.junit.Assert;
import org.junit.Test;

public class PointTest {
	@Test
	public void normalizeTest() {
		Point point = Point.fromDegrees( 45, 517 );
		Assert.assertEquals( 45, point.getLatitude(), 0 );
		Assert.assertEquals( 157, point.getLongitude(), 0 );
	}

	@Test
	public void computeDestinationTest() {
		Point point = Point.fromDegrees( 45, 4 );

		Point destination = point.computeDestination( 100, 45 );

		Assert.assertEquals( destination.getLatitudeRad(), 0.796432523, 0.00001 );
		Assert.assertEquals( destination.getLongitudeRad(), 0.08568597, 0.00001 );
	}

	@Test
	public void distanceToPoint() {
		Point point = Point.fromDegrees( 45, 4 );
		Point point2 = Point.fromDegrees( 46, 14 );

		double distance = point.getDistanceTo( point2 );

		Assert.assertEquals( distance, 786.7, 0.1 );
	}
}
