package smartspace.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import smartspace.data.ElementEntity;
import smartspace.logic.ElementService;

@RestController
public class ElementController {

	private ElementService elementService;

	@Autowired
	public ElementController(ElementService elementService) {
		this.elementService = elementService;
	}

	@RequestMapping(
			path = "/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] importElements(
			@RequestBody ElementBoundary[] elementBoundaryArray,
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail) {
		
		List<ElementEntity> elementEntites = new ArrayList<ElementEntity>();
		for (ElementBoundary element : elementBoundaryArray) {
			elementEntites.add(element.convertToEntity());
		}

		return this.elementService.importElements(
				elementEntites,
				adminSmartspace + "#" + adminEmail)
				.stream()
				.map(ElementBoundary::new)
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(
			path = "/smartspace/admin/elements/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] exportElements(
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		return this.elementService.ExportElements(size, page, adminSmartspace + "#" + adminEmail)
				.stream()
				.map(ElementBoundary::new)
				.collect(Collectors.toList())
				.toArray(new ElementBoundary[0]);
	}

	@RequestMapping(
			path = "/smartspace/elements/{managerSmartspace}/{managerEmail}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary createElementByPlayer(
			@RequestBody ElementBoundary elementBoundary,
			@PathVariable("managerSmartspace") String managerSmartspace,
			@PathVariable("managerEmail") String managerEmail) {

		return new ElementBoundary(this.elementService.createElement(elementBoundary.convertToEntity(),
				managerSmartspace + "#" + managerEmail));

	}

	@RequestMapping(
			path = "/smartspace/elements/{managerSmartspace}/{managerEmail}/{elementSmartspace}/{elementId}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void UpdateAnElement(@PathVariable("managerSmartspace") String managerSmartspace,
			@PathVariable("managerEmail") String managerEmail,
			@PathVariable("elementSmartspace") String elementSmartspace,
			@PathVariable("elementId") String elementId,
			@RequestBody ElementBoundary element) {

		this.elementService.update(element.convertToEntity(), elementSmartspace + "#" + elementId,
				managerSmartspace + "#" + managerEmail);
	}

	@RequestMapping(
			path = "/smartspace/elements/{userSmartspace}/{userEmail}/{elementSmartspace}/{elementId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary RetriveSpecificElement(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("elementSmartspace") String elementSmartspace,
			@PathVariable("elementId") String elementId) {

		return new ElementBoundary(
				this.elementService.returnElement(elementSmartspace, elementId, userSmartspace + "#" + userEmail));
	}
	
	@RequestMapping(
			path = "/smartspace/elements/{userSmartspace}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ElementBoundary[] getAllElelemntBySearch(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "search", required = false, defaultValue = "") String search,
			@RequestParam(name = "value", required = false, defaultValue = "") String value,
			@RequestParam(name = "x", required = false, defaultValue = "0") double x,
			@RequestParam(name = "y", required = false, defaultValue = "0") double y,
			@RequestParam(name = "distance", required = false, defaultValue = "0") double distance,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size) {

		if (search.equals("")) {
			return this.elementService.getElements(size, page, userSmartspace + "#" + userEmail).stream()
					.map(ElementBoundary::new).collect(Collectors.toList()).toArray(new ElementBoundary[0]);
		} else if (search.equals("name")) {

			return this.elementService.getElementsByName(value, size, page, userSmartspace + "#" + userEmail).stream()
					.map(ElementBoundary::new).collect(Collectors.toList()).toArray(new ElementBoundary[0]);
		}

		else if (search.equals("type")) {

			return this.elementService.getElementsByType(value, size, page, userSmartspace + "#" + userEmail).stream()
					.map(ElementBoundary::new).collect(Collectors.toList()).toArray(new ElementBoundary[0]);
		}

		return this.elementService.getElementsByDistance(x, y, distance, size, page, userSmartspace + "#" + userEmail)
				.stream().map(ElementBoundary::new).collect(Collectors.toList()).toArray(new ElementBoundary[0]);
	}
}