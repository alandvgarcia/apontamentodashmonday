package com.solinftec.apontamentosmondaydash

import com.solinftec.apontamentosmondaydash.model.Apontamento
import com.solinftec.apontamentosmondaydash.util.parseToApontamentoGroupedByPerson
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayInputStream

actual fun readExcelFile(byteArray: ByteArray): Pair<List<Apontamento>, String> {

    val inputStream = ByteArrayInputStream(byteArray)

    val data = XSSFWorkbook(inputStream).let { workbook ->

        val firstSheet = workbook.getSheetAt(0)
        val groupedData = mutableMapOf<Pair<String, String>, MutableMap<String, MutableList<List<String>>>>()
        var taskName = ""

        for (i in 2..firstSheet.lastRowNum) {
            val currentRow = firstSheet.getRow(i) ?: continue

            val nameCell = currentRow.getCell(0)
            val dateCell = currentRow.getCell(2)

            if (nameCell != null && nameCell.cellType == CellType.STRING) {

                val name = nameCell.stringCellValue

                if (name == "Started By") {
                    taskName = firstSheet.getRow(i - 1).getCell(0).stringCellValue
                } else if (name == "Total") {
                    taskName = ""
                }

                if (name == "Started By" || name == "Total" || name.isEmpty()) {
                    continue
                }


                if (dateCell != null &&
                    dateCell.cellType == CellType.STRING
                ) {
                    val date = dateCell.stringCellValue
                    groupedData
                        .getOrPut(name to taskName) { mutableMapOf() }
                        .getOrPut(date) { mutableListOf() }
                        .add(currentRow.map { it.stringCellValue })
                }
            }
        }

        groupedData
    }


    val listApontamento = parseToApontamentoGroupedByPerson(data)

    return listApontamento


}




