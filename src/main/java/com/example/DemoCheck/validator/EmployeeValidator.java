package com.example.DemoCheck.validator;

import com.example.DemoCheck.entity.Employee;
import com.example.DemoCheck.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("beforeCreateEmployeeValidator")
public class EmployeeValidator implements Validator {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Employee.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Employee emp = (Employee) target;

        // 1. Self-reporting check
        if (emp.getManager() != null && emp.getEmployeeNumber() != null) {
            if (emp.getEmployeeNumber().equals(emp.getManager().getEmployeeNumber())) {
                errors.rejectValue("manager", "self.report", "An employee cannot report to themselves.");
            }

            // 2. Existence check
            // If Jackson mapped the ID to an object but it has no data,
            // we check if it actually exists in the DB.
            Integer managerId = emp.getManager().getEmployeeNumber();
            if (managerId != null && !employeeRepository.existsById(managerId)) {
                errors.rejectValue("manager", "manager.notfound", "Employee with ID " + managerId + " does not exist.");
            }
        }
    }
}
