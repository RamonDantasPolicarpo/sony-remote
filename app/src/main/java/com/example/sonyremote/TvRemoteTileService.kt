package com.example.sonyremote

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

/**
 * Bloco de Configurações Rápidas (Quick Settings) que apenas abre a MainActivity.
 * Não envia nenhum código IR — só traz o app para a frente.
 *
 * TileService só existe a partir do Android 7.0 (API 24). Em versões anteriores
 * o app continua instalando e funcionando normalmente, apenas sem o tile.
 */
class TvRemoteTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        // Deixa o tile sempre "ativo" visualmente.
        qsTile?.let { tile ->
            tile.state = Tile.STATE_ACTIVE
            tile.updateTile()
        }
    }

    override fun onClick() {
        super.onClick()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // Android 14+ (API 34): é obrigatório usar um PendingIntent.
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            // Android 7.0 a 13: usa o Intent direto.
            @Suppress("DEPRECATION", "StartActivityAndCollapseDeprecated")
            startActivityAndCollapse(intent)
        }
    }
}