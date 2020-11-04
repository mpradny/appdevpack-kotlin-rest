package net.pristo.kotlin.proton.rest

import net.pristo.kotlin.proton.repository.EmployeeRepository
import net.pristo.kotlin.proton.repository.Employee
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/employee")
class EmployeeResource(val employeeRepository: EmployeeRepository) {

    @GetMapping
    fun listEmployees() : ResponseEntity<List<Employee>> {
        return ResponseEntity(employeeRepository.findAll(), HttpStatus.OK)
    }
}