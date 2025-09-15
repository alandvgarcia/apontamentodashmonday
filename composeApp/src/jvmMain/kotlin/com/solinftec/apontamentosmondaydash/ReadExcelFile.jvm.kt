package com.solinftec.apontamentosmondaydash

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.ByteArrayInputStream

actual fun readExcelFile(byteArray: ByteArray): Pair<List<Apontamento>, String> {

    val inputStream = ByteArrayInputStream(byteArray)

    val data = XSSFWorkbook(inputStream).let { workbook ->

        val firstSheet = workbook.getSheetAt(0)
        val groupedData = mutableMapOf<String, MutableMap<String, MutableList<List<String>>>>()

        for (i in 0..firstSheet.lastRowNum) {
            val currentRow = firstSheet.getRow(i) ?: continue

            val nameCell = currentRow.getCell(0)
            val dateCell = currentRow.getCell(2)

            if (nameCell != null && dateCell != null &&
                nameCell.cellType == CellType.STRING &&
                dateCell.cellType == CellType.STRING) {

                val name = nameCell.stringCellValue
                val date = dateCell.stringCellValue

                if (name == "Started By" || name == "Total" || name.isEmpty()) {
                    continue
                }

                // Adiciona a linha ao mapa, criando as estruturas aninhadas se necessário
                groupedData
                    .getOrPut(name) { mutableMapOf() }
                    .getOrPut(date) { mutableListOf() }
                    .add(currentRow.map { it.stringCellValue })
            }
        }

        groupedData
    }



    val listApontamento = parseToApontamentoGroupedByPerson(data)

    return listApontamento


}




