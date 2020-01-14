package smartspace.data;

import java.util.Date;
import java.util.Map;

/*
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import smartspace.dao.rdb.MapToJsonConverter;
*/

// after change to mongoDB
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ACTIONS")
public class ActionEntity implements SmartspaceEntity<String> {
	
	@Transient
	private String actionSmartspace;
	
	@Transient
	private String actionId;
	
	private String elementSmartspace;
	private String elementId;
	private String playerSmartspace;
	private String playerEmail;
	private String actionType;
	private Date creationTimestamp;
	private Map<String, Object> moreAttributes;
	// mongoDB requirement this attribute 
	private String key;

	public ActionEntity() {
	}

	public ActionEntity(String elementSmartspace, String elementId, String playerSmartspace, String playerEmail,
			String actionType, Date creationTimestamp, Map<String, Object> moreAttributes) {
		super();
		this.elementSmartspace = elementSmartspace;
		this.elementId = elementId;
		this.playerSmartspace = playerSmartspace;
		this.playerEmail = playerEmail;
		this.actionType = actionType;
		this.creationTimestamp = creationTimestamp;
		this.moreAttributes = moreAttributes;
	}

	public String getActionSmartspace() {
		return actionSmartspace;
	}

	public void setActionSmartspace(String actionSmartspace) {
		this.actionSmartspace = actionSmartspace;
	}

	public String getElementSmartspace() {
		return elementSmartspace;
	}

	public void setElementSmartspace(String elementSmartspace) {
		this.elementSmartspace = elementSmartspace;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getPlayerSmartspace() {
		return playerSmartspace;
	}

	public void setPlayerSmartspace(String playerSmartspace) {
		this.playerSmartspace = playerSmartspace;
	}

	public String getPlayerEmail() {
		return playerEmail;
	}

	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Map<String, Object> getMoreAttributes() {
		return moreAttributes;
	}

	public void setMoreAttributes(Map<String, Object> moreAttributes) {
		this.moreAttributes = moreAttributes;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	@Id
	public String getKey() {
		return this.key;
	}

	@Override
	public void setKey(String key) {
		this.actionSmartspace = key.split("#")[0];
		this.actionId = key.split("#")[1];
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionEntity other = (ActionEntity) obj;
		if (creationTimestamp == null) {
			if (other.creationTimestamp != null)
				return false;
		} else if (!creationTimestamp.equals(other.creationTimestamp))
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
		if (moreAttributes == null) {
			if (other.moreAttributes != null)
				return false;
		} else if (!moreAttributes.equals(other.moreAttributes))
			return false;
		if (actionSmartspace == null) {
			if (other.actionSmartspace != null)
				return false;
		} else if (!actionSmartspace.equals(other.actionSmartspace))
			return false;
		if (actionId == null) {
			if (other.actionId != null)
				return false;
		} else if (!actionId.equals(other.actionId))
			return false;
		if (playerSmartspace == null) {
			if (other.playerSmartspace != null)
				return false;
		} else if (!playerSmartspace.equals(other.playerSmartspace))
			return false;
		if (playerEmail == null) {
			if (other.playerEmail != null)
				return false;
		} else if (!playerEmail.equals(other.playerEmail))
			return false;
		if (actionType == null) {
			if (other.actionType != null)
				return false;
		} else if (!actionType.equals(other.actionType))
			return false;
		return true;
	}

}