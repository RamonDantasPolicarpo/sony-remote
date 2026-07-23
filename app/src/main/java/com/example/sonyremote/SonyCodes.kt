package com.example.sonyremote

/**
 * Códigos SIRC da Sony (endereço de TV = 1).
 *
 * VERIFICADOS (banco Flipper-IRDB, funcionam na linha Bravia):
 *   POWER, MUTE, VOL+, VOL-, CH+, CH-
 *
 * CONJUNTO PADRÃO SONY (navegação) — quase certo, mas TESTE na sua TV.
 * Se algum botão não responder, é só trocar o número aqui embaixo por outro
 * do conjunto Sony e recompilar. Todos os valores estão em hexadecimal (0x..).
 */
object SonyCodes {
    const val ADDR_TV = 1

    // --- verificados ---
    const val POWER = 0x15
    const val MUTE = 0x14
    const val VOL_UP = 0x12
    const val VOL_DOWN = 0x13
    const val CH_UP = 0x10
    const val CH_DOWN = 0x11

    // --- power discreto (opcional; liga/desliga sem alternar) ---
    const val POWER_ON = 0x2E
    const val POWER_OFF = 0x2F

    // --- navegação (testar) ---
    const val UP = 0x74
    const val DOWN = 0x75
    const val LEFT = 0x34
    const val RIGHT = 0x33
    const val OK = 0x65      // Enter / Confirmar
    const val HOME = 0x60    // menu inicial
    const val BACK = 0x63    // Return / Voltar
    const val INPUT = 0x25   // troca de entrada (TV/Video, HDMI)
    const val DISPLAY = 0x3A // informações na tela
}
