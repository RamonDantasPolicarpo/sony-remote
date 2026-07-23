package com.example.sonyremote

import android.content.Context
import android.hardware.ConsumerIrManager

/**
 * Transmissor do protocolo Sony SIRC (12 bits, portadora de 40 kHz).
 *
 * A TV recebe: um "header" longo, depois 7 bits de COMANDO e 5 bits de
 * ENDEREÇO (para TV o endereço é 1), sempre do bit menos significativo (LSB)
 * para o mais significativo. Cada bit é um pulso de luz + uma pausa.
 */
class SircTransmitter(context: Context) {

    private val ir =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    /** true se o celular tem emissor de infravermelho utilizável. */
    val hasEmitter: Boolean
        get() = ir?.hasIrEmitter() == true

    // Tempos em microssegundos (padrão SIRC).
    private val freqHz = 40_000
    private val headerMark = 2400
    private val space = 600
    private val oneMark = 1200
    private val zeroMark = 600
    private val framePeriod = 45_000 // início-a-início entre repetições

    /**
     * Envia um comando.
     * @param command código de 7 bits do botão
     * @param address dispositivo (1 = TV)
     * @param repeats a Sony costuma precisar de 3 repetições para registrar
     */
    fun send(command: Int, address: Int = 1, repeats: Int = 3) {
        val emitter = ir ?: return
        if (!emitter.hasIrEmitter()) return

        // Mede um quadro para calcular a pausa entre repetições.
        val oneFrame = ArrayList<Int>()
        appendFrame(oneFrame, command, address, space)
        val gap = (framePeriod - oneFrame.sum()).coerceAtLeast(10_000)

        val pattern = ArrayList<Int>()
        for (i in 0 until repeats) {
            val trailing = if (i == repeats - 1) space else gap
            appendFrame(pattern, command, address, trailing)
        }

        emitter.transmit(freqHz, pattern.toIntArray())
    }

    /** Monta um quadro alternando marca/pausa; a última pausa é [trailingSpace]. */
    private fun appendFrame(out: ArrayList<Int>, command: Int, address: Int, trailingSpace: Int) {
        out.add(headerMark); out.add(space)

        val bits = ArrayList<Int>(12)
        for (i in 0 until 7) bits.add((command shr i) and 1) // comando: 7 bits, LSB primeiro
        for (i in 0 until 5) bits.add((address shr i) and 1) // endereço: 5 bits, LSB primeiro

        for (j in bits.indices) {
            out.add(if (bits[j] == 1) oneMark else zeroMark)
            out.add(if (j == bits.lastIndex) trailingSpace else space)
        }
    }
}
