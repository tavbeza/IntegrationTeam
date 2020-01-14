package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.AdvancedActionDao;
import smartspace.data.ActionEntity;

@Repository
public class RdbActionDao implements AdvancedActionDao {

	private ActionCrud actionCrud;
	private String smartspace;

	@Autowired
	@Value("${smartspace.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}
	
	@Autowired
	public RdbActionDao(ActionCrud actionCrud) {
		super();
		this.actionCrud = actionCrud;
		if(this.actionCrud.count() > 0) {
			List<ActionEntity> allActions = new ArrayList<>();
			this.actionCrud.findAll().forEach(allActions::add);
			
			List<ActionEntity> filteredActionsBySmartspace = new ArrayList<>();
			for(ActionEntity action : allActions) {
				action.setKey(action.getKey());
				if(action.getActionSmartspace().equals("2019B.dana.zuka")) {
					filteredActionsBySmartspace.add(action);
				}
			}
			GeneratedId.setNumOfActions(filteredActionsBySmartspace.size());
		}
	}

	@Override
	@Transactional
	public ActionEntity create(ActionEntity actionEntity) {
		if (actionEntity.getActionSmartspace() != null && actionEntity.getActionId() != null)
			actionEntity.setKey(actionEntity.getActionSmartspace() + "#" + actionEntity.getActionId());
		else
			actionEntity.setKey(smartspace + "#" + GeneratedId.getNextActionValue());
		
		// SQL: INSERT
		if (!this.actionCrud.existsById(actionEntity.getKey())) {
			return this.actionCrud.save(actionEntity);
		} else {
			throw new RuntimeException("action already exists with key: " + actionEntity.getKey());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ActionEntity> readById(String actiontKey) {
		// SQL: SELECT
		return this.actionCrud.findById(actiontKey);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll() {
		List<ActionEntity> rv = new ArrayList<ActionEntity>();
		// SQL: SELECT
		this.actionCrud.findAll().forEach(rv::add);
		return rv;
	}

	@Override
	@Transactional
	public void deleteAll() {
		// SQL: DELETE
		this.actionCrud.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll(int size, int page) {
		return this.actionCrud.findAll(PageRequest.of(page, size)).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readAll(String sortBy, int size, int page) {
		return this.actionCrud.findAll(PageRequest.of(page, size, Direction.ASC, sortBy)).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionsByPlayerEmailPattern(String pattern, int size, int page) {
		return this.actionCrud.findAllByPlayerEmailLike("%" + pattern + "%", PageRequest.of(page, size));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionsByIdPattern(String pattern, int size, int page) {
		return this.actionCrud.findByElementIdLike("%" + pattern + "%", PageRequest.of(page, size));
	}

	@Override
	public List<ActionEntity> readActionsByIdPattern(String pattern, String sortBy, int size, int page) {
		// change
		return this.actionCrud.findByElementIdLike("%" + pattern + "%",
				PageRequest.of(page, size, Direction.ASC, sortBy));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActionEntity> readActionsWithAvailableFromInRange(Date fromDate, Date toDate, int size, int page) {
		return this.actionCrud.findAllByCreationTimestampBetween(fromDate, toDate, PageRequest.of(page, size));
	}

	@Override
	@Transactional
	public void update(ActionEntity updateActionEntity) {
		ActionEntity existing = this.readById(updateActionEntity.getKey())
				.orElseThrow(() -> new RuntimeException("no element entity with key: " + updateActionEntity.getKey()));

		if (updateActionEntity.getActionType() != null) {
			existing.setActionType(updateActionEntity.getActionType());
		}
		if (updateActionEntity.getMoreAttributes() != null) {
			existing.setMoreAttributes(updateActionEntity.getMoreAttributes());
		}
		if (updateActionEntity.getCreationTimestamp() != null) {
			existing.setCreationTimestamp(updateActionEntity.getCreationTimestamp());
		}
		if (updateActionEntity.getPlayerEmail() != null) {
			existing.setPlayerEmail(updateActionEntity.getPlayerEmail());
		}
		if (updateActionEntity.getPlayerSmartspace() != null) {
			existing.setPlayerSmartspace(updateActionEntity.getPlayerSmartspace());
		}
		if (updateActionEntity.getElementId() != null) {
			existing.setElementId((updateActionEntity.getElementId()));
		}
		if (updateActionEntity.getElementSmartspace() != null) {
			existing.setElementSmartspace((updateActionEntity.getElementSmartspace()));
		}

		// SQL: UPDATE
		this.actionCrud.save(existing);
	}

	@Override
	@Transactional
	public void deleteByKey(String key) {
		if (this.actionCrud.existsById(key)) {
			this.actionCrud.deleteById(key);
		}

	}

}
