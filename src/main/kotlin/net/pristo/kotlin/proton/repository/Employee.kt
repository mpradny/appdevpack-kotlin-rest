package net.pristo.kotlin.proton.repository

import com.hcl.domino.db.model.Database
import com.hcl.domino.db.model.Document
import com.hcl.domino.db.model.OptionalItemNames
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository


fun Document.getItemValueString(name:String) : String =
    this.getItemByName(name).first().value.first().toString()

data class Employee(val firstName: String, val lastName: String, val ssn: String) {
    constructor(doc: Document) : this(doc.getItemValueString("FirstName"),
        doc.getItemValueString("LastName"),
        "")
}

@Repository
class EmployeeRepository(val db: Database) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun findAll() : List<Employee> {
        log.debug("Starting user loading")
        val query = "Form = 'Contact'"
        val docs: List<Document> = db.readDocuments(query, OptionalItemNames(listOf("FirstName", "LastName"))).get()
        log.debug("Done loading")

        return docsToEmployees(docs)
    }

    fun docsToEmployees(docs: List<Document?>): MutableList<Employee> =
        docs.asSequence()
            .filterNotNull()
            .mapTo(mutableListOf()) { docToEmployee(it) }

    fun docToEmployee(doc: Document): Employee = Employee(doc)


}