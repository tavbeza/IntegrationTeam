package smartspace.layout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import smartspace.data.ElementEntity;
import smartspace.data.Location;

public class ElementBoundary {

	private Map<String, String> key;
	private String elementType;
	private String name;
	private boolean expired;
	private Date created;
	private Map<String, String> creator;
	private Map<String, Double> latlng;
	private Map<String, Object> elementProperties;

	public ElementBoundary() {
	}

	public ElementBoundary(ElementEntity entity) {
		this.key = new HashMap<>();
		this.key.put("id", entity.getElementId());
		this.key.put("smartspace", entity.getElementSmartspace());
		this.elementType = entity.getType();
		this.name = entity.getName();
		this.expired = entity.getExpired();
		this.created = entity.getCreationTimestamp();
		this.creator = new HashMap<>();
		this.creator.put("email", entity.getCreatorEmail());
		this.creator.put("smartspace", entity.getCreatorSmartspace());
		this.latlng = new HashMap<>();
		this.latlng.put("lat", Double.valueOf(entity.getLocation().getX()));
		this.latlng.put("lng", Double.valueOf(entity.getLocation().getY()));
		this.elementProperties = new HashMap<>();
		this.elementProperties = entity.getMoreAttributes();
	}

	public Map<String, String> getKey() {
		return key;
	}

	public void setKey(Map<String, String> key) {
		this.key = key;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Map<String, String> getCreator() {
		return creator;
	}

	public void setCreator(Map<String, String> creator) {
		this.creator = creator;
	}

	public Map<String, Double> getLatlng() {
		return latlng;
	}

	public void setLatlng(Map<String, Double> latlng) {
		this.latlng = latlng;
	}

	public Map<String, Object> getElementProperties() {
		return elementProperties;
	}

	public void setElementProperties(Map<String, Object> elementProperties) {
		this.elementProperties = elementProperties;
	}

	public ElementEntity convertToEntity() {
		ElementEntity entity = new ElementEntity();

		if (this.key != null && this.key.get("smartspace") != null && this.key.get("id") != null
				&& !this.key.get("smartspace").trim().isEmpty() && !this.key.get("id").trim().isEmpty())
			entity.setKey(this.key.get("smartspace") + "#" + this.key.get("id"));
		
		Location l = new Location();
		if (this.latlng != null && this.latlng.get("lat") != null && this.latlng.get("lng") != null) {
			l.setX(this.latlng.get("lat"));
			l.setY(this.latlng.get("lng"));
		}
		entity.setLocation(l);
		entity.setName(this.name);
		entity.setType(this.elementType);
		entity.setExpired(this.expired);
		if (this.creator != null && this.creator.get("smartspace") != null && this.creator.get("email") != null
				&& !this.creator.get("smartspace").trim().isEmpty() && !this.creator.get("email").trim().isEmpty()) {
			entity.setCreatorSmartspace(this.creator.get("smartspace"));
			entity.setCreatorEmail(this.creator.get("email"));

		}
		entity.setCreationTimestamp(this.created);
		
		if(this.elementProperties != null)
			entity.setMoreAttributes(this.elementProperties);
		else
			entity.setMoreAttributes(new HashMap<>());
		
		return entity;
	}

	public String toString() {
		return "ElementBoundary [elementSmartspace=" + this.key.get("smartspace") + ", elementId=" + this.key.get("id")
				+ ", latlng=(" + this.latlng.get("lat") + ", " + this.latlng.get("lng") + ")" + ", name=" + name
				+ ", elementType=" + this.elementType + ", created=" + this.created + ", expired=" + expired
				+ ", creatorSmartspace=" + this.creator.get("smartspce") + ", creatorEmail=" + this.creator.get("email")
				+ ", elementProperties=" + this.elementProperties + "]";
	}
}
