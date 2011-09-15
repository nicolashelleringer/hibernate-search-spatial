package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.Map;

public class SpatialFieldBridge implements FieldBridge {
	@Override
	public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
		Point point= (Point) value;

		if(point==null) return;

		Map<Integer,String> cellIds= GridManager.getGridCellIds(point,0,16);

		for(int i= 0; i <= 16; i++) {
			document.add(new Field( "HSSI_"+Integer.toString(i)+"_"+name,
				cellIds.get(i),
				Field.Store.YES,
				Field.Index.NOT_ANALYZED_NO_NORMS
			));
		}
	}
}
