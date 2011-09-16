package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.OpenBitSet;

import java.io.IOException;

public class DoubleRangeFilter extends Filter {
	public DoubleRangeFilter( Rectangle rectangle, String fieldName ) {

	}

	@Override
	public DocIdSet getDocIdSet( IndexReader reader ) throws IOException {
		return null;
	}

}
