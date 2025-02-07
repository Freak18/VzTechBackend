package entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("employees")
@Data
public class Employee {

    @PrimaryKey
    private UUID id;
    private String name;
    private String department;
    private double salary;

    //Create constructor with parameters
    public Employee(String name, String department, double salary) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.department = department;
        this.salary = salary;
    }



}
