package Repository;

import entity.Employee;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EmployeeRepository extends ReactiveCassandraRepository<Employee, UUID> {

    Flux<Employee> findByDepartment(String department);
}
