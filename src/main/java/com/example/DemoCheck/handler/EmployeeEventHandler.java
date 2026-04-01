package com.example.DemoCheck.handler;

import com.example.DemoCheck.entity.Employee;
import com.example.DemoCheck.repository.EmployeeRepository;
import com.example.DemoCheck.repository.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RepositoryEventHandler(Employee.class)
public class EmployeeEventHandler {

    public static final ThreadLocal<Integer> currentEmployeeId = new ThreadLocal<>();

    @Autowired
    private OfficeRepository officeRepository;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Autowired
    private EmployeeRepository employeeRepository;

    @HandleBeforeCreate
    public void beforeCreate(Employee employee) {
        checkDuplicate(employee);
        validate(employee);
    }

    @HandleBeforeSave
    public void beforeSave(Employee employee) {
        validate(employee);
    }

    private void checkDuplicate(Employee employee) {
        if (employee.getEmployeeNumber() != null) {

            currentEmployeeId.set(employee.getEmployeeNumber()); // ✅ store always

            if (employeeRepository.existsById(employee.getEmployeeNumber())) {
                throw new IllegalArgumentException(
                        "Employee already exists with id: " + employee.getEmployeeNumber()
                );
            }
        }
    }

    private void validate(Employee employee) {
        if (employee.getEmployeeNumber() == null) {
            throw new IllegalArgumentException("employeeNumber cannot be null");
        }

        employee.setFirstName(normalize(employee.getFirstName(), "firstName"));
        employee.setLastName(normalize(employee.getLastName(), "lastName"));

        employee.setJobTitle(normalize(employee.getJobTitle(), "jobTitle"));

        String email = employee.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
        else if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Must be a valid email format");
        }

        // --- REVISED EMAIL VALIDATION ---
        if (employee.getEmail() != null) {
            String currentEmail = employee.getEmail().trim();

            // Use the List version to avoid the "Non-unique result" crash
            List<Employee> existingEmployees = employeeRepository.findByEmail(currentEmail);

            for (Employee existing : existingEmployees) {
                // If we find an employee with this email that is NOT the current employee
                if (!existing.getEmployeeNumber().equals(employee.getEmployeeNumber())) {
                    throw new IllegalArgumentException("Email already exists: " + currentEmail +
                            " (Assigned to Employee #" + existing.getEmployeeNumber() + ")");
                }
            }

            employee.setEmail(currentEmail);
        }

        // --- OFFICE VALIDATION ---
        // 1. CATCH THE NULL CASE FIRST
        if (employee.getOffice() == null) {
            throw new IllegalArgumentException("Given Office Does Not Exist! try adding employee with valid Existing Office!");
        }else{
            String officeCode = employee.getOffice().getOfficeCode();

            if (officeCode == null || !officeRepository.existsById(officeCode)) {
                throw new IllegalArgumentException("Office not found with code: " + officeCode);
            }
        }

        // --- MANAGER VALIDATION ---

        if (employee.getManager() != null) {
            Integer managerId = employee.getManager().getEmployeeNumber();

            if (!employeeRepository.existsById(managerId)) {
                throw new IllegalArgumentException("Manager not found with ID: " + managerId);
            }

            if (managerId.equals(employee.getEmployeeNumber())) {
                throw new IllegalArgumentException("Employee cannot report to themselves");
            }
        }

    }

    private String normalize(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

}
