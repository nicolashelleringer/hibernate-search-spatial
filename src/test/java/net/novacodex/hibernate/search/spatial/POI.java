package net.novacodex.hibernate.search.spatial;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Indexed
public class POI {
	@Id
	Integer id;

	@Field
	String name;

	@Field
	double latitude;
	@Field
	double longitude;

	@Field
	@FieldBridge(impl=SpatialFieldBridge.class)
	@Embedded
	public SpatialIndexable getLocation(){
		return new SpatialIndexable() {
			@Override
			public double getLatitude() {
				return latitude;
			}

			@Override
			public double getLongitude() {
				return longitude;
			}
		};
	}

	public POI(Integer id, String name, double latitude, double longitude) {
		this.id= id;
		this.name= name;
		this.latitude= latitude;
		this.longitude=	longitude;
	}

	public POI(){}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
}
