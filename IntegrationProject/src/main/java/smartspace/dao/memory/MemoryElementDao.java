package smartspace.dao.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;

//@Repository
public class MemoryElementDao implements ElementDao<String> {

	private Map<String, ElementEntity> memory;
	private AtomicLong serial;
	private String smartspace;

	public MemoryElementDao() {
		this.memory = Collections.synchronizedSortedMap(new TreeMap<>());
		this.serial = new AtomicLong(1L);
	}

	@Value("${smartspace.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Override
	public ElementEntity create(ElementEntity elementEntity) {
		elementEntity.setKey(smartspace + "#" + serial.getAndIncrement());
		this.memory.put(elementEntity.getKey(), elementEntity);
		elementEntity.setElementSmartspace(this.smartspace);
		return elementEntity;
	}

	@Override
	public Optional<ElementEntity> readById(String elementKey) {
		ElementEntity element = this.memory.get(elementKey);
		if (element != null) {
			return Optional.of(element);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public List<ElementEntity> readAll() {
		return new ArrayList<>(this.memory.values());
	}

	@Override
	public void update(ElementEntity updateElementEntity) {
		ElementEntity existing = this.readById(updateElementEntity.getKey())
				.orElseThrow(() -> new RuntimeException("no element entity with key: " + updateElementEntity.getKey()));

		if (updateElementEntity.getLocation() != null) {
			existing.setLocation(updateElementEntity.getLocation());
		}
		if (updateElementEntity.getName() != null) {
			existing.setName(updateElementEntity.getName());
		}
		if (updateElementEntity.getType() != null) {
			existing.setType(updateElementEntity.getType());
		}
		if (updateElementEntity.getMoreAttributes() != null) {
			existing.setMoreAttributes(updateElementEntity.getMoreAttributes());
		}
		if (updateElementEntity.getCreationTimestamp() != null) {
			existing.setCreationTimestamp(updateElementEntity.getCreationTimestamp());
		}
		if (updateElementEntity.getCreatorEmail() != null) {
			existing.setCreatorEmail(updateElementEntity.getCreatorEmail());
		}
		if (updateElementEntity.getCreatorSmartspace() != null) {
			existing.setCreatorSmartspace(updateElementEntity.getCreatorSmartspace());
		}
		existing.setExpired(updateElementEntity.getExpired());

	}

	@Override
	public void deleteByKey(String elementKey) {
		if (this.memory.containsKey(elementKey))
			this.memory.remove(elementKey);
	}

	@Override
	public void delete(ElementEntity elementEntity) {
		if (this.memory.containsValue(elementEntity))
			this.memory.remove(elementEntity.getKey());
	}

	@Override
	public void deleteAll() {
		this.memory.clear();
	}

}
