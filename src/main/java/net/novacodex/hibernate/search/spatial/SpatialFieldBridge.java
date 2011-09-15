package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.solr.util.NumberUtils;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.Map;

public class SpatialFieldBridge implements FieldBridge {
	public static final int MIN_GRID_LEVEL = 0;
	public static final int MAX_GRID_LEVEL = 16;

	@Override
	public void set( String name, Object value, Document document, LuceneOptions luceneOptions ) {
		Point point = (Point) value;

		if ( point == null )
			return;

		Map<Integer, String> cellIds = GridManager.getGridCellsIds( point, MIN_GRID_LEVEL, MAX_GRID_LEVEL );

		for ( int i = MIN_GRID_LEVEL; i <= MAX_GRID_LEVEL; i++ ) {
			document.add( new Field( "HSSI_" + Integer.toString( i ) + "_" + name, cellIds.get( i ), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS ) );
		}

		document.add( new Field( "HSSI_Latitude_" + name, NumberUtils.double2sortableStr( point.getLatitude() ), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS ) );

		document.add( new Field( "HSSI_Longitude_" + name, NumberUtils.double2sortableStr( point.getLongitude() ), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS ) );
	}
}
