package smartspace.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import smartspace.data.ActionEntity;

public interface AdvancedActionDao extends ActionDao {

	public List<ActionEntity> readAll(int size, int page);

	public List<ActionEntity> readAll(String sortBy, int size, int page);

	public List<ActionEntity> readActionsByPlayerEmailPattern(String pattern, int size, int page);

	public List<ActionEntity> readActionsByIdPattern(String pattern, int size, int page);

	public List<ActionEntity> readActionsByIdPattern(String pattern, String sortBy, int size, int page);

	public List<ActionEntity> readActionsWithAvailableFromInRange(Date fromDate, Date toDate, int size, int page);

	public void update(ActionEntity entity);

	public Optional<ActionEntity> readById(String actiontKey);

	public void deleteByKey(String key);

	
}
