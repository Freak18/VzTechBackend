package Controller;

import entity.Employee;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import service.EmployeeService;

import java.util.UUID;

@Controller
public class EmployeeGraphQLController {
    private final EmployeeService employeeService;


    public EmployeeGraphQLController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @QueryMapping
    public Flux<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @QueryMapping
    public Flux<Employee> getEmployeeByDepartment(String department) {
        return employeeService.getEmployeeByDepartment(department);
    }

    @QueryMapping
    public Mono<Employee> getEmployeeById(@Argument UUID id) {
        return employeeService.getEmployeeById(id);
    }

    @MutationMapping
    public Mono<Employee> createEmployee(@Argument String name, @Argument String department, @Argument double salary) {
        return employeeService.createEmployee(new Employee(name,department,salary));
    }

    @MutationMapping
    public Mono<Employee> updateEmployee(@Argument UUID id, @Argument String name, @Argument String department, @Argument double salary) {
        return employeeService.updateEmployee(id, new Employee(name, department, salary));
    }

    @MutationMapping
    public Mono<String> deleteemployee(@Argument UUID id) {
        return employeeService.deleteEmployee(id).thenReturn("Employee with id " + id + " deleted successfully");
    }


}
