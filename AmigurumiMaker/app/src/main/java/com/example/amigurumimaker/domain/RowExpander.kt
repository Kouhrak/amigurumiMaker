package com.example.amigurumimaker.domain

import com.example.amigurumimaker.domain.model.RowDef

/**
 * Expands [RowDef] ranges into individual row definitions.
 * A RowDef with startRow=1, endRow=3 produces three RowDefs
 * with row numbers 1, 2, and 3, each carrying the same tokens.
 */
object RowExpander {

    fun expand(rowDef: RowDef): List<RowDef> {
        return (rowDef.startRow..rowDef.endRow).map { rowNumber ->
            RowDef(startRow = rowNumber, endRow = rowNumber, tokens = rowDef.tokens)
        }
    }
}
