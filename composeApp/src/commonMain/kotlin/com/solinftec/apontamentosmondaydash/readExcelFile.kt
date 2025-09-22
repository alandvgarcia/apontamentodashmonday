package com.solinftec.apontamentosmondaydash

import com.solinftec.apontamentosmondaydash.model.Apontamento

expect fun readExcelFile(byteArray: ByteArray): Pair<List<Apontamento>, String>