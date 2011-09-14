package net.novacodex.GridManager;

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
	@FieldBridge(impl=SpatialFieldBridge.class)
	@Embedded
	Point position;

	public POI(Integer id, String name, Point position) {
		this.id= id;
		this.name= name;
		this.position= position;
	}

	public POI(){}

	public Integer getId() {
		return id;
	}

	public Point getPosition() {
		return position;
	}

	public String getName() {
		return name;
	}
}
