package net.pristo.kotlin.proton.rest

import com.hcl.domino.db.model.BulkOperationException
import net.pristo.kotlin.proton.repository.Employee
import net.pristo.kotlin.proton.repository.EmployeeRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/employee")
class EmployeeResource(val employeeRepository: EmployeeRepository) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping
    fun listEmployees() : ResponseEntity<List<Employee>> {
        return ResponseEntity(employeeRepository.findAll(), HttpStatus.OK)
    }

    @PostMapping
    fun createUser(@Valid @RequestBody employee: Employee): ResponseEntity<Employee> {
        log.debug("REST request to save User : $employee")
        return ResponseEntity(employeeRepository.create(employee), HttpStatus.OK)
    }

    @ExceptionHandler(value = [(BulkOperationException::class)])
    fun handleDominoException(ex: BulkOperationException): ResponseEntity<Any> {
        val docErrors = ex.documentExceptionList.map { it -> "doc:${it.unid} : ${it.message} " }
        if (!ex.documentExceptionList.isEmpty()) {
            log.error(docErrors.toString())
            return ResponseEntity(docErrors, HttpStatus.INTERNAL_SERVER_ERROR)
        }
        throw ex
    }
}