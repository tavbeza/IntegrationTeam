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

import smartspace.dao.AdvancedElementDao;
import smartspace.data.ElementEntity;

@Repository
public class RdbElementDao implements AdvancedElementDao<String> {

	private ElementCrud elementCrud;
	private String smartspace;

	@Value("${smartspace.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}
	
	@Autowired
	public RdbElementDao(ElementCrud elementCrud) {
		super();
		this.elementCrud = elementCrud;
		if(this.elementCrud.count() > 0) {
			List<ElementEntity> allElements = new ArrayList<>();
			this.elementCrud.findAll().forEach(allElements::add);
			
			List<ElementEntity> filteredElementsBySmartspace = new ArrayList<>();
			for(ElementEntity element:allElements) {
				element.setKey(element.getKey());
				if(element.getElementSmartspace().equals("2019B.dana.zuka")) {
					filteredElementsBySmartspace.add(element);
				}
			}			
			GeneratedId.setNumOfElements(filteredElementsBySmartspace.size());
		}
	}


	@Override
	@Transactional
	public ElementEntity create(ElementEntity element) {
		if (element.getElementSmartspace() != null && element.getElementId() != null)
			element.setKey(element.getElementSmartspace() + "#" + element.getElementId());
		else
			element.setKey(smartspace + "#" + GeneratedId.getNextElementValue());
				
		// SQL: INSERT
		if (!this.elementCrud.existsById(element.getKey())) {
			element.setCreationTimestamp(new Date());
			return this.elementCrud.save(element);
		} else {
			throw new RuntimeException("element already exists with key: " + element.getKey());
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Optional<ElementEntity> readById(String elementKey) {
		// SQL: SELECT
		return this.elementCrud.findById(elementKey);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readAll() {
		List<ElementEntity> rv = new ArrayList<>();
		// SQL: SELECT
		this.elementCrud.findAll().forEach(rv::add);
		return rv;
	}

	@Override
	@Transactional
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

		// SQL: UPDATE
		this.elementCrud.save(existing);
	}

	@Override
	@Transactional
	public void deleteByKey(String elementKey) {
		if (this.elementCrud.existsById(elementKey))
			this.elementCrud.deleteById(elementKey);
	}

	@Override
	@Transactional
	public void delete(ElementEntity elementEntity) {
		if (this.elementCrud.existsById(elementEntity.getKey())) {
			this.elementCrud.delete(elementEntity);
		}
	}

	@Override
	@Transactional
	public void deleteAll() {
		this.elementCrud.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readAll(int size, int page) {
		return this.elementCrud.findAll(PageRequest.of(page, size)).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readAll(String sortBy, int size, int page) {
		return this.elementCrud.findAll(PageRequest.of(page, size, Direction.ASC, sortBy)).getContent();
	}

	@Override

	@Transactional(readOnly = true)
	public List<ElementEntity> readElementByTextPattern(String pattern, int size, int page) {
		return this.elementCrud.findAllByNameLike("%" + pattern + "%", PageRequest.of(page, size));
	}

	@Override
	@Transactional(readOnly = true)
	public List<ElementEntity> readElementsWithCreationTimestampInRange(Date fromDate, Date toDate, int size,
			int page) {
		return this.elementCrud.findAllByCreationTimestampBetween(fromDate, toDate, PageRequest.of(page, size));

	}

	@Override
	public List<ElementEntity> readElementByTextPattern(String pattern, String sortBy, int size, int page) {
		return this.elementCrud.findAllByNameLike("%" + pattern + "%",
				PageRequest.of(page, size, Direction.ASC, sortBy));
	}

	@Override
	public List<ElementEntity> readElementsByName(String value, int size, int page) {
		return this.elementCrud.findAllByNameLikeAndExpiredIsFalse(value, PageRequest.of(page, size));

	}

	@Override
	public List<ElementEntity> readElementsByType(String value, int size, int page) {
		return this.elementCrud.findAllByTypeLikeAndExpiredIsFalse(value, PageRequest.of(page, size));
	}

	@Override
	public List<ElementEntity> readElementsByDistance(double x, double y, double distance, int size, int page) {
		double minX = x - distance;
		double maxX = x + distance;
		double minY = y - distance;
		double maxY = y + distance;
		return this.elementCrud.findAllByLocation_XBetweenAndLocation_YBetweenAndExpiredIsFalse(minX, maxX, minY, maxY,
				PageRequest.of(page, size));
	}
}
