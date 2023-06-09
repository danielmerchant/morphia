package dev.morphia.example;

import com.mongodb.client.result.UpdateResult;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dev.morphia.test.TestBase;

import org.testng.annotations.Test;

import static dev.morphia.query.filters.Filters.gt;
import static dev.morphia.query.filters.Filters.lte;
import static dev.morphia.query.updates.UpdateOperators.inc;
import static org.testng.Assert.assertEquals;

/**
 * This class is used in the Quick Tour documentation and is used to demonstrate various Morphia features.
 */
@Test
public class QuickTourTest extends TestBase {
    public void demo() {
        final Datastore datastore = Morphia.createDatastore(getMongoClient());

        // tell morphia where to find your classes
        // can be called multiple times with different packages or classes
        datastore.getMapper().mapPackage("dev.morphia.example");

        // create the Datastore connecting to the database running on the default port on the local host
        datastore.getDatabase().drop();
        datastore.ensureIndexes();

        final Employee elmer = datastore.save(new Employee("Elmer Fudd", 50000.0));
        final Employee daffy = datastore.save(new Employee("Daffy Duck", 40000.0));
        final Employee pepe = datastore.save(new Employee("Pepé Le Pew", 25000.0));

        elmer.getDirectReports().add(daffy);
        elmer.getDirectReports().add(pepe);

        datastore.save(elmer);

        Query<Employee> query = datastore.find(Employee.class);
        final long employees = query.count();

        assertEquals(employees, 3);

        long underpaid = datastore.find(Employee.class)
                .filter(lte("salary", 30000))
                .count();
        assertEquals(underpaid, 1);

        final Query<Employee> underPaidQuery = datastore.find(Employee.class)
                .filter(lte("salary", 30000));
        final UpdateResult results = underPaidQuery.update(inc("salary", 10000))
                .execute();

        assertEquals(results.getModifiedCount(), 1);

        datastore.find(Employee.class)
                .filter(gt("salary", 100000))
                .findAndDelete();
    }
}
