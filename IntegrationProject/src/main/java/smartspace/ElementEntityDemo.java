package smartspace;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import smartspace.aop.PerformanceMonitorAdvice;
import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactoryImpl;

//@Component
@Profile("production")
public class ElementEntityDemo implements CommandLineRunner {

	private EntityFactoryImpl factory;
	private ElementDao<String> elementDao;
	Log log = LogFactory.getLog(PerformanceMonitorAdvice.class);

	public ElementEntityDemo() {
	}
	
	@Autowired
	public ElementEntityDemo(EntityFactoryImpl factory, ElementDao<String> elementDao) {
		this.factory = factory;
		this.elementDao = elementDao;
	}

	@Override
	public void run(String... args) throws Exception {
		String tasksBaseName = "task";
		String smartspace = "2019b.dana.zuka";
		// List of cleaners
		String[] cleaningEmployees = { "2019b.dana.zuka@avi", "2019b.dana.zuka@yossi", "2019b.dana.zuka@shimon" };

		// Create 'moreAttributes' Map for task
		Map<String, Object> taskDetails = new HashMap<>();

		taskDetails.put("departmentName", "clean");
		taskDetails.put("taskDescribe", "clean the...");
		taskDetails.put("priority", 1);
		taskDetails.put("status", enumStatus.IN_PROGRESS);
		taskDetails.put("employeesAssigned", Arrays.asList(cleaningEmployees));

		// create task by 'createNewElementEntity' method
		ElementEntity task1 = this.factory.createNewElement(tasksBaseName, "Task", new Location(1.0, 1.0),
				new Date(), "tavb@mail.afeka.ac.il", smartspace, false, taskDetails);

		task1 = this.elementDao.create(task1);
		
		ElementEntity task2 = this.factory.createNewElement(tasksBaseName, "Task", new Location(2.0, 2.0),
				new Date(), "tavb@mail.afeka.ac.il", smartspace, false, taskDetails);

		task2 = this.elementDao.create(task2);

		// print tasks
		log.debug("stored task1:\n" + task1);
		log.debug("stored task2:\n" + task2);

		Map<String, Object> updatedDetails = new HashMap<>(task1.getMoreAttributes());
		updatedDetails.put("gender", "male");
		updatedDetails.put("age", 32);

		ElementEntity update = new ElementEntity();
		update.setKey(task1.getKey());
		update.setMoreAttributes(updatedDetails);

		this.elementDao.update(update);

		// Check the method 'update'
		Optional<ElementEntity> elementOp = this.elementDao.readById(task1.getKey());
		if (elementOp.isPresent()) {
			task1 = elementOp.get();
		} else {
			throw new RuntimeException("Error! element vanished after update");
		}

		log.debug("updated element:\n" + task1);

		// List of tasks
		String[] departmentTasks = { task1.getName(), task2.getName() };

		// Create 'moreAttributes' Map for task
		Map<String, Object> departmentDetails = new HashMap<>();
		departmentDetails.put("employeesList", Arrays.asList(cleaningEmployees));
		departmentDetails.put("departmentTasks", Arrays.asList(departmentTasks));

		ElementEntity department = this.factory.createNewElement("Clean" , "Department", new Location(3.0, 3.0),
				new Date(), "tavb@mail.afeka.ac.il", smartspace, false, departmentDetails);

		department = this.elementDao.create(department);

		// Print department
		log.debug("stored department:\n" + department);

		// Delete all the elements
		this.elementDao.deleteAll();

		// Check the method 'deleteAll'
		if (this.elementDao.readAll().isEmpty()) {
			log.debug("successfully deleted all the elements");
		} else {
			throw new RuntimeException("Error! the elements still exist after deletion");
		}

	}

}
