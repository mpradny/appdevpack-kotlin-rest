package net.pristo.kotlin.proton.repository

import com.hcl.domino.db.model.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.util.*


fun Document.getItemValueString(name: String) : String =
    this.getItemByName(name)?.first()?.value?.first()?.toString() ?: "NOT_FOUND"

data class Employee(val id: String? = null, val firstName: String, val lastName: String, val ssn: String) {
    constructor(doc: Document) : this(doc.unid,
        doc.getItemValueString("FirstName"),
        doc.getItemValueString("LastName"),
        doc.getItemValueString("SSN")
    )
}

@Repository
class EmployeeRepository(val db: Database) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val form = "Contact"
    private val docItems = listOf("FirstName", "LastName","SSN")

    fun findAll() : List<Employee> {
        log.debug("Starting user loading")
        val query = "Form = '$form'"
        val docs: List<Document> = db.readDocuments(query, OptionalItemNames(docItems)).get()
        log.debug("Done loading")

        return docsToEmployees(docs)
    }

    fun find(id: String): Employee {
        val fullDoc = db.readDocumentByUnid(id, docItems).get()
        return docToEmployee(fullDoc)
    }

    fun create(employee: Employee): Employee {
        log.debug("Starting user creation")

        val createDoc = employeeToDoc(employee)
        val res = db.createDocument(createDoc).get()

        log.debug("Done creating, read it back")

        return find(res.unid)
    }

    private fun employeeToDoc(employee: Employee): Document {
        val itemList: MutableList<Item<*>> = ArrayList()
        itemList.add(TextItem("Form", form))
        itemList.add(TextItem("FirstName", employee.firstName))
        itemList.add(TextItem("LastName", employee.lastName))
        itemList.add(TextItem("SSN", employee.ssn, ItemFlags.ITEM_FLAG_ENCRYPT))

        return Document(itemList)
    }

    fun delete(id: String) {
        db.deleteDocumentByUnid(id).get()
    }

    fun updateEmployee(id: String, employee: Employee): Employee {
        val updateDoc = employeeToDoc(employee)

        db.replaceDocumentsByUnid(mapOf(id to updateDoc.items)).get()

        return find(id)
    }

    fun docsToEmployees(docs: List<Document?>): MutableList<Employee> =
        docs.asSequence()
            .filterNotNull()
            .mapTo(mutableListOf()) { docToEmployee(it) }

    fun docToEmployee(doc: Document): Employee = Employee(doc)



}