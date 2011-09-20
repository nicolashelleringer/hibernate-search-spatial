package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;

import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.ParameterizedBridge;

import java.util.Map;

public class SpatialFieldBridge implements FieldBridge, ParameterizedBridge {

	public static final int MIN_GRID_LEVEL = 0;
	public static final int MAX_GRID_LEVEL = 16;

	private int min_grid_level= MIN_GRID_LEVEL;
	private int max_grid_level= MAX_GRID_LEVEL;

	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		if ( value != null ) {

			SpatialIndexable spatialIndexable = ( SpatialIndexable ) value;

			Point point = Point.fromDegrees( spatialIndexable.getLatitude(), spatialIndexable.getLongitude() );

			Map<Integer, String> cellIds = GridManager.getGridCellsIds( point, min_grid_level, max_grid_level );

			for ( int i = min_grid_level; i <= max_grid_level; i++ ) {
				document.add(
						new Field(
								FieldUtils.formatFieldName( i, name ),
								cellIds.get( i ),
								Field.Store.YES,
								Field.Index.NOT_ANALYZED_NO_NORMS
						)
				);
			}

			document.add(
					new NumericField( FieldUtils.formatLatitude( name ), Field.Store.YES, true ).setDoubleValue(
							point.getLatitude()
					)
			);

			document.add(
					new NumericField( FieldUtils.formatLongitude( name ), Field.Store.YES, true ).setDoubleValue(
							point.getLongitude()
					)
			);
		}
	}

	@Override
	public void setParameterValues(Map parameters) {
		Object min_grid_level= parameters.get("min_grid_level");
		if(min_grid_level instanceof Integer) {
			this.min_grid_level= (Integer)min_grid_level;
		}
		Object max_grid_level= parameters.get("min_grid_level");
		if(max_grid_level instanceof Integer) {
			this.max_grid_level= (Integer)max_grid_level;
		}
	}
}