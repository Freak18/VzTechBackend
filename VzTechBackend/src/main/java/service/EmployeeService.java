package service;

import entity.Employee;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EmployeeService {

    Flux<Employee> getAllEmployees();
    Mono<Employee> getEmployeeById(UUID id);

    Flux<Employee> getEmployeeByDepartment(String department);

    Mono<Employee> createEmployee(Employee employee);

    Mono<Employee> updateEmployee(UUID id, Employee employee);

    Mono<Void> deleteEmployee(UUID id);

}
