package smartspace.logic;

import java.util.List;

import smartspace.data.ActionEntity;

public interface ActionService {
	public ActionEntity writeAction(ActionEntity actionEntity);

	public List<ActionEntity> getActions(int size, int page);

	public List<ActionEntity> getActions(String sortBy, int size, int page);

	public List<ActionEntity> getActionsByPattern(String pattern, String sortBy, int size, int page);

	public void update(ActionEntity entity);

	public void deleteByKey(String key);

	public List<ActionEntity> importActions(ActionEntity[] actionEntityArray, String key);

	public List<ActionEntity> exportActions(int size, int page, String key);

	public ActionEntity invokeAnAction(ActionEntity convertToEntity);
}
