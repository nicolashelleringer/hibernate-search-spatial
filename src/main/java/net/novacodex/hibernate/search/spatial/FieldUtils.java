package net.novacodex.hibernate.search.spatial;

abstract class FieldUtils {

	private static final String FIELDNAME_TEMPLATE = "HSSI_%s_%s";

	private static final String LATITUDE_TEMPLATE = "HSSI_Latitude_%s";

	private static final String LONGITUDE_TEMPLATE = "HSSI_Longitude_%s";

	private static final String GRID_CELL_ID_TEMPLATE = "%s|%s";

	public static String formatFieldName(int gridLevel, String fieldName) {
		return String.format( FIELDNAME_TEMPLATE, gridLevel, fieldName );
	}

	public static String formatLatitude(String fieldName) {
		return String.format( LATITUDE_TEMPLATE, fieldName );
	}

	public static String formatLongitude(String fieldName) {
		return String.format( LONGITUDE_TEMPLATE, fieldName );
	}

	public static String formatGridCellId(int xIndex, int yIndex) {
		return String.format( GRID_CELL_ID_TEMPLATE, xIndex, yIndex );
	}

}