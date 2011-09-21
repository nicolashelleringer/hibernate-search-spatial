package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.document.Document;

import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.ParameterizedBridge;

import java.util.Map;

/**
 * @author Nicolas Helleringer
 *         <p/>
 *         Hibernate Search field bridge, binding a SpatialIndexable to Grid field in the index
 */
public class SpatialFieldBridge implements FieldBridge, ParameterizedBridge {

	public static final int MIN_GRID_LEVEL = 0;
	public static final int MAX_GRID_LEVEL = 16;

	private int min_grid_level = MIN_GRID_LEVEL;
	private int max_grid_level = MAX_GRID_LEVEL;

	/**
	 * Actual overridden method that does the indexing
	 *
	 * @param name of the field
	 * @param value of the field
	 * @param document document beeing indexed
	 * @param luceneOptions current indexing options and accessors
	 */
	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		if ( value != null ) {

			SpatialIndexable spatialIndexable = ( SpatialIndexable ) value;

			Point point = Point.fromDegrees( spatialIndexable.getLatitude(), spatialIndexable.getLongitude() );

			Map<Integer, String> cellIds = GridHelper.getGridCellsIds( point, min_grid_level, max_grid_level );

			for ( int i = min_grid_level; i <= max_grid_level; i++ ) {
				luceneOptions.addFieldToDocument( GridHelper.formatFieldName( i, name ), cellIds.get( i ), document );
			}

			luceneOptions.addNumericFieldToDocument( GridHelper.formatLatitude( name ), point.getLatitude(), document );

			luceneOptions.addNumericFieldToDocument(
					GridHelper.formatLongitude( name ),
					point.getLongitude(),
					document
			);

		}
	}

	/**
	 * Override method for default min and max grid level
	 *
	 * @param parameters Map containing the min_grid_level and max_grid_level values
	 */
	@Override
	public void setParameterValues(Map parameters) {
		Object min_grid_level = parameters.get( "min_grid_level" );
		if ( min_grid_level instanceof Integer ) {
			this.min_grid_level = ( Integer ) min_grid_level;
		}
		Object max_grid_level = parameters.get( "min_grid_level" );
		if ( max_grid_level instanceof Integer ) {
			this.max_grid_level = ( Integer ) max_grid_level;
		}
	}
}