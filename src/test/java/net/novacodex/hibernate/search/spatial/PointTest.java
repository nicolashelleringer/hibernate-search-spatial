package net.novacodex.hibernate.search.spatial;

import org.junit.Assert;
import org.junit.Test;

public class PointTest {
	@Test
	public void normalizeTest() {
		Point point= new Point(45,517);
		Assert.assertTrue( point.getLatitude() == 45 );
		Assert.assertTrue(point.getLongitude()==157);
	}

	@Test
	public void computeDestinationTest() {
		Point point= new Point(45,4);

		Point destination= point.computeDestination( 100, 45);

		Assert.assertEquals(Math.toRadians(destination.getLatitude()),0.796432523,0.00001);
		Assert.assertEquals(Math.toRadians(destination.getLongitude()),0.08568597,0.00001);
	}

	@Test
	public void distanceToPoint() {
		Point point= new Point(45,4);
		Point point2= new Point(46,14);

		double distance= point.distanceToPoint(point2);

		Assert.assertEquals(distance,786.7,0.1);
	}
}
