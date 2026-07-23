package com.example.sonyremote

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/**
 * Tela única do controle. A interface é montada em código (sem XML) para
 * ficar com o menor número possível de peças que poderiam quebrar no build.
 */
class MainActivity : Activity() {

    private lateinit var ir: SircTransmitter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ir = SircTransmitter(this)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#101418"))
            setPadding(dp(20), dp(24), dp(20), dp(24))
        }

        // Aviso caso o aparelho não tenha emissor IR.
        if (!ir.hasEmitter) {
            root.addView(TextView(this).apply {
                text = "Este celular não tem emissor de infravermelho."
                setTextColor(Color.parseColor("#FF6B6B"))
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, dp(16))
            })
        }

        // Linha superior: Power / Input / Mute
        // O power é ícone vetorial (renderiza igual em qualquer aparelho).
        root.addView(row(
            iconBtn(R.drawable.ic_power, SonyCodes.POWER, "#E23B3B"),
            btn("Fonte", SonyCodes.INPUT),
            btn("🔇", SonyCodes.MUTE)
        ))

        root.addView(spacer())

        // D-pad — setas como ícones vetoriais, todas no mesmo estilo.
        root.addView(centerRow(iconBtn(R.drawable.ic_up, SonyCodes.UP)))
        root.addView(row(
            iconBtn(R.drawable.ic_left, SonyCodes.LEFT),
            btn("OK", SonyCodes.OK, "#2D7DD2"),
            iconBtn(R.drawable.ic_right, SonyCodes.RIGHT)
        ))
        root.addView(centerRow(iconBtn(R.drawable.ic_down, SonyCodes.DOWN)))

        root.addView(spacer())

        // Home / Voltar / Info
        root.addView(row(
            btn("Home", SonyCodes.HOME),
            btn("Voltar", SonyCodes.BACK),
            btn("Info", SonyCodes.DISPLAY)
        ))

        root.addView(spacer())

        // Volume e Canal
        root.addView(row(
            btn("VOL +", SonyCodes.VOL_UP),
            btn("CH +", SonyCodes.CH_UP)
        ))
        root.addView(row(
            btn("VOL −", SonyCodes.VOL_DOWN),
            btn("CH −", SonyCodes.CH_DOWN)
        ))

        setContentView(root)
    }

    /** Cria um botão de TEXTO que envia [command] ao ser tocado. */
    private fun btn(label: String, command: Int, colorHex: String = "#1E2833"): Button {
        return Button(this).apply {
            text = label
            textSize = 18f
            setTextColor(Color.WHITE)
            // Remove o contorno/sombra padrão do Button para o canto ficar limpo.
            stateListAnimator = null
            styleKey(this, command, colorHex)
        }
    }

    /** Cria um botão de ÍCONE (vetorial) que envia [command] ao ser tocado. */
    private fun iconBtn(iconRes: Int, command: Int, colorHex: String = "#1E2833"): ImageView {
        return ImageView(this).apply {
            setImageResource(iconRes)
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(dp(18), dp(18), dp(18), dp(18))
            styleKey(this, command, colorHex)
        }
    }

    /** Aplica o visual comum (fundo arredondado + toque) e a ação de enviar IR. */
    private fun styleKey(v: View, command: Int, colorHex: String) {
        val base = GradientDrawable().apply {
            setColor(Color.parseColor(colorHex))
            cornerRadius = dp(16).toFloat()
        }
        v.background = RippleDrawable(
            ColorStateList.valueOf(Color.parseColor("#40FFFFFF")), base, null
        )
        val lp = LinearLayout.LayoutParams(0, dp(64), 1f)
        lp.setMargins(dp(6), dp(6), dp(6), dp(6))
        v.layoutParams = lp
        v.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            if (ir.hasEmitter) {
                ir.send(command, SonyCodes.ADDR_TV)
            } else {
                Toast.makeText(this@MainActivity, "Sem emissor IR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun row(vararg views: View): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            views.forEach { addView(it) }
        }
    }

    /** Fica com o botão no centro (usado no ▲ e ▼ do d-pad). */
    private fun centerRow(view: View): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            addView(View(this@MainActivity), LinearLayout.LayoutParams(0, 1, 1f))
            addView(view)
            addView(View(this@MainActivity), LinearLayout.LayoutParams(0, 1, 1f))
        }
    }

    private fun spacer(): View =
        View(this).apply { layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, dp(10)) }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()
}
