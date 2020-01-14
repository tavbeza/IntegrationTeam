package smartspace.logic;

import java.util.List;

import smartspace.data.ElementEntity;

public interface ElementService {

	public ElementEntity writeElement(ElementEntity elementEntity);

	public List<ElementEntity> getElements(int size, int page);

	public List<ElementEntity> getElements(String sortBy, int size, int page);

	public List<ElementEntity> getElementsByPattern(String pattern, String sortBy, int size, int page);

	public void deleteByKey(String key);

	public List<ElementEntity> importElements(List<ElementEntity> convertToEntity, String key);

	public List<ElementEntity> ExportElements(int size, int page, String key);

	public ElementEntity createElement(ElementEntity entity, String key);

	public void update(ElementEntity entity, String elementKey, String key);

	public ElementEntity returnElement(String elementSmartspace, String elementId, String key);

	public List<ElementEntity> getElementsByName(String value, int size, int page, String key);

	public List<ElementEntity> getElementsByType(String value, int size, int page, String key);

	public List<ElementEntity> getElementsByDistance(double x, double y, double distance, int size, int page,
			String key);

	public List<ElementEntity> getElements(int size, int page, String key);

}