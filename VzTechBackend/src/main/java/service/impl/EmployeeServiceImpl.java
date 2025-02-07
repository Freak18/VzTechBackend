package service.impl;

import Repository.EmployeeRepository;
import entity.Employee;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import service.EmployeeService;

import java.time.Duration;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    private  final ReactiveRedisTemplate<String, Employee> reactiveRedisTemplate;
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);  //


    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ReactiveRedisTemplate reactiveRedisTemplate) {
        this.employeeRepository = employeeRepository;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Flux<Employee> getAllEmployees() {
        String cacheKey = "all_employees";

        return reactiveRedisTemplate.opsForList().range(cacheKey, 0, -1)
                .switchIfEmpty(employeeRepository.findAll()
                        .collectList()
                        .flatMapMany(employees -> {
                            return reactiveRedisTemplate.opsForList().rightPushAll(cacheKey, employees)
                                    .then(reactiveRedisTemplate.expire(cacheKey, CACHE_TTL))
                                    .thenMany(Flux.fromIterable(employees));
                        })
                );
    }


    @Override
    public Mono<Employee> getEmployeeById(UUID id) {
        String cacheKey = "employee_" + id;
        return reactiveRedisTemplate.opsForValue().get(cacheKey)
                .switchIfEmpty(employeeRepository.findById(id)
                        .flatMap(employee -> {
                            return reactiveRedisTemplate.opsForValue().set(cacheKey, employee)
                                    .then(reactiveRedisTemplate.expire(cacheKey, CACHE_TTL).thenReturn(employee));
                        }));
    }

    @Override
    public Flux<Employee> getEmployeeByDepartment(String department) {
        String cacheKey = "employees_" + department;
        return reactiveRedisTemplate.opsForList().range(cacheKey, 0, -1)
                .switchIfEmpty(employeeRepository.findByDepartment(department)
                        .collectList()
                        .flatMap(employees -> reactiveRedisTemplate.opsForList().rightPushAll(cacheKey, employees)
                                .then(reactiveRedisTemplate.expire(cacheKey, CACHE_TTL))
                                .thenReturn(employees))
                        .flatMapMany(Flux::fromIterable));
    }
    @Override
    public Mono<Employee> createEmployee(Employee employee) {
        return employeeRepository.save(employee).flatMap(saved-> {
            return reactiveRedisTemplate.delete("all_employees").thenReturn(saved);
        });
    }

    @Override
    public Mono<Employee> updateEmployee(UUID id, Employee employee) {
        return employeeRepository.findById(id)
                .flatMap(existingEmployee -> {
                    existingEmployee.setName(employee.getName());
                    existingEmployee.setDepartment(employee.getDepartment());
                    existingEmployee.setSalary(employee.getSalary());
                    return employeeRepository.save(existingEmployee);
                })
                .flatMap(updateEmployee->{
                    return reactiveRedisTemplate.delete("all_employees").thenReturn(updateEmployee);
                });
    }

    @Override
    public Mono<Void> deleteEmployee(UUID id) {
        return employeeRepository.deleteById(id)
                .then(reactiveRedisTemplate.delete("employee_" + id).then())
                .then(reactiveRedisTemplate.delete("all_employees").then());
    }


}
