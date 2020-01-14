package smartspace.data;

import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ELEMENTS")
public class ElementEntity implements SmartspaceEntity<String> {

	@Transient
	private String elementSmartspace;
	
	@Transient
	private String elementId;
	
	private Location location;
	private String name;
	private String type;
	private Date creationTimestamp;
	private boolean expired;
	private String creatorSmartspace; // the smartSpace of the other user who create the element
	private String creatorEmail;
	private Map<String, Object> moreAttributes;
	// mongoDB requirement this attribute 
	private String key;

	public ElementEntity() {
	}

	public ElementEntity(String name, String type, Location location, Date creationTimestamp, String creatorEmail,
			String creatorSmartspace, boolean expired, Map<String, Object> moreAttributes) {
		super();
		this.location = location;
		this.name = name;
		this.type = type;
		this.creationTimestamp = creationTimestamp;
		this.expired = expired;
		this.creatorSmartspace = creatorSmartspace;
		this.creatorEmail = creatorEmail;
		this.moreAttributes = moreAttributes;

	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getElementSmartspace() {
		return this.elementSmartspace;
	}

	public void setElementSmartspace(String smartspace) {
		this.elementSmartspace = smartspace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public boolean getExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getCreatorSmartspace() {
		return creatorSmartspace;
	}

	public void setCreatorSmartspace(String creatorSmartspace) {
		this.creatorSmartspace = creatorSmartspace;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public Map<String, Object> getMoreAttributes() {
		return moreAttributes;
	}

	public void setMoreAttributes(Map<String, Object> moreAttributes) {
		this.moreAttributes = moreAttributes;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Id
	public String getKey() {
		return this.key;
	}

	@Override
	public void setKey(String key) {
		this.elementSmartspace = key.split("#")[0];
		this.elementId = key.split("#")[1];
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElementEntity other = (ElementEntity) obj;
		if (creationTimestamp == null) {
			if (other.creationTimestamp != null)
				return false;
		} else if (!creationTimestamp.equals(other.creationTimestamp))
			return false;
		if (creatorEmail == null) {
			if (other.creatorEmail != null)
				return false;
		} else if (!creatorEmail.equals(other.creatorEmail))
			return false;
		if (creatorSmartspace == null) {
			if (other.creatorSmartspace != null)
				return false;
		} else if (!creatorSmartspace.equals(other.creatorSmartspace))
			return false;
		if (elementId == null) {
			if (other.elementId != null)
				return false;
		} else if (!elementId.equals(other.elementId))
			return false;
		if (elementSmartspace == null) {
			if (other.elementSmartspace != null)
				return false;
		} else if (!elementSmartspace.equals(other.elementSmartspace))
			return false;
		if (expired != other.expired)
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (moreAttributes == null) {
			if (other.moreAttributes != null)
				return false;
		} else if (!moreAttributes.equals(other.moreAttributes))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ElementEntity [elementSmartspace=" + getElementSmartspace() + ", elementId=" + getKey() + ", location="
				+ getLocation() + ", name=" + getName() + ", type=" + getType() + ", creationTimestamp="
				+ getCreationTimestamp() + ", expired=" + getExpired() + ", creatorSmartspace=" + getCreatorSmartspace()
				+ ", creatorEmail=" + getCreatorEmail() + ", moreAttributes=" + getMoreAttributes() + "]";
	}
}