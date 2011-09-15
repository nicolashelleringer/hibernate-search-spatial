package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.index.IndexReader.*;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.OpenBitSet;

import java.io.IOException;
import java.util.List;

public class GridFilter extends Filter{

	private List<String> gridCellsIds;
	private String fieldName;

	public GridFilter(List<String> gridCellsIds, String fieldName) {
		this.gridCellsIds= gridCellsIds;
		this.fieldName= fieldName;
	}

	@Override
	public DocIdSet getDocIdSet( IndexReader reader ) throws IOException {
		if(gridCellsIds.size()==0) return null;

		OpenBitSet matchedDocumentsIds= new OpenBitSet(reader.maxDoc());
		for(int i=0;i<gridCellsIds.size();i++) {
			Term gridCellTerm= new Term(fieldName,gridCellsIds.get( i ));
			TermDocs gridCellsDocs= reader.termDocs( gridCellTerm );
			if(gridCellsDocs!=null) {
				while(gridCellsDocs.next()){
					matchedDocumentsIds.fastSet(gridCellsDocs.doc());
				}
			}
		}

		return matchedDocumentsIds;
	}
}