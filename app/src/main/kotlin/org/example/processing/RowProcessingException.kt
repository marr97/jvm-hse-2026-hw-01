package org.example.processing

import org.example.model.ErrorKind

class RowProcessingException(
    val kind: ErrorKind,
    override val message: String,
) : Exception(message)
