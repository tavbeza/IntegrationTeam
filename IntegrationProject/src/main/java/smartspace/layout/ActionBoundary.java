package smartspace.layout;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import smartspace.data.ActionEntity;

public class ActionBoundary {

	private Map<String, String> actionKey;
	private String type;
	private Date created;
	private Map<String, String> element;
	private Map<String, String> player;
	private Map<String, Object> properties;

	public ActionBoundary() {
	}

	public ActionBoundary(ActionEntity entity) {
		this.actionKey = new HashMap<>();
		this.actionKey.put("smartspace", entity.getActionSmartspace());
		this.actionKey.put("id", entity.getActionId());
		this.type = entity.getActionType();
		this.created = entity.getCreationTimestamp();
		this.element = new HashMap<>();
		this.element.put("id", entity.getElementId());
		this.element.put("smartspace", entity.getElementSmartspace());
		this.player = new HashMap<>();
		this.player.put("smartspace", entity.getPlayerSmartspace());
		this.player.put("email", entity.getPlayerEmail());
		this.properties = entity.getMoreAttributes();
	}

	public Map<String, String> getActionKey() {
		return actionKey;
	}

	public void setActionKey(Map<String, String> actionKey) {
		this.actionKey = actionKey;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Map<String, String> getElement() {
		return element;
	}

	public void setElement(Map<String, String> element) {
		this.element = element;
	}

	public Map<String, String> getPlayer() {
		return player;
	}

	public void setPlayer(Map<String, String> player) {
		this.player = player;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public ActionEntity convertToEntity() {
		ActionEntity entity = new ActionEntity();
		if (this.actionKey != null && this.actionKey.get("smartspace")!= null && this.actionKey.get("id")!= null
				&& !this.actionKey.get("smartspace").trim().isEmpty()
				&& !this.actionKey.get("id").trim().isEmpty() )
			entity.setKey(this.actionKey.get("smartspace") + "#" + this.actionKey.get("id"));
		
		if (this.element != null && this.element.get("smartspace")!= null && this.element.get("id")!= null) {
			entity.setElementId(this.element.get("id"));
			entity.setElementSmartspace(this.element.get("smartspace"));
		}
		if (this.player != null && this.player.get("smartspace")!= null && this.player.get("email")!= null) {
			entity.setPlayerEmail(this.player.get("email"));
			entity.setPlayerSmartspace(this.player.get("smartspace"));
		}

		entity.setActionType(this.type);
		entity.setCreationTimestamp(this.created);
		
		if(this.properties != null)
			entity.setMoreAttributes(this.properties);
		else
			entity.setMoreAttributes(new HashMap<>());
		
		return entity;
	}
	
	@Override
	public String toString() {
		return "ActionBoundary [actionSmartspace=" + this.actionKey.get("smartspace") + ", userId=" + this.actionKey.get("id") + ", elementSmartspace="
				+ this.element.get("smartspace") + ", elementId=" + this.element.get("id") + ", playerSmartspace=" + this.player.get("smartspace")
				+ ", playerEmail=" + this.player.get("email") + ",actionType=" + this.type + ",creationTimestamp"
				+ this.created + ",moreAttributes" + this.properties + "]";
	}
}
