package smartspace.dao;

import java.util.Date;
import java.util.List;

import smartspace.data.ElementEntity;

public interface AdvancedElementDao<K> extends ElementDao<K> {

	public List<ElementEntity> readAll(int size, int page);

	public List<ElementEntity> readAll(String sortBy, int size, int page);

	public List<ElementEntity> readElementByTextPattern(String pattern, int size, int page);

	public List<ElementEntity> readElementByTextPattern(String pattern, String sortBy, int size, int page);

	public List<ElementEntity> readElementsWithCreationTimestampInRange(Date fromDate, Date toDate, int size, int page);

	public List<ElementEntity> readElementsByName(String value, int size, int page);

	public List<ElementEntity> readElementsByType(String value, int size, int page);

	public List<ElementEntity> readElementsByDistance(double x, double y, double distance, int size, int page);

}
