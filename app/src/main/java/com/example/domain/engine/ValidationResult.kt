package com.example.domain.engine

data class ValidationError(
    val rowIndex: Int,
    val field: String,
    val message: String
)

data class ValidationWarning(
    val rowIndex: Int,
    val message: String
)

data class ValidationResult(
    val isValid: Boolean,
    val totalRows: Int,
    val validRows: Int,
    val errors: List<ValidationError> = emptyList(),
    val warnings: List<ValidationWarning> = emptyList(),
    val startDate: Long? = null,
    val endDate: Long? = null,
    val summary: String = ""
)
