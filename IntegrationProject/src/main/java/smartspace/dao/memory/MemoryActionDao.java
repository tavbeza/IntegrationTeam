package smartspace.dao.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;

import smartspace.dao.ActionDao;
import smartspace.data.ActionEntity;

// @Repository
public class MemoryActionDao implements ActionDao{
	
	private Map<String, ActionEntity> memory;
	private AtomicLong serial;
	private String smartspace;
	
	public MemoryActionDao(){
		this.memory = Collections.synchronizedSortedMap(new TreeMap<>());
		this.serial = new AtomicLong(1L);
	}
	
	@Value("${smartspace.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Override
	public ActionEntity create(ActionEntity actionEntity) {
		actionEntity.setKey(smartspace + "#" + serial.getAndIncrement());
		actionEntity.setActionSmartspace(smartspace);
		this.memory.put(actionEntity.getKey(), actionEntity);
		return actionEntity;
	}

	@Override
	public List<ActionEntity> readAll() {
		return new ArrayList<>(this.memory.values());
	}

	@Override
	public void deleteAll() {
		this.memory.clear();
	}

	
	
}
