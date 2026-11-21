# Controle Sony (KDL-32W655D) — app IR em Kotlin

App mínimo, sem anúncios e sem bibliotecas externas. Usa só o emissor de
infravermelho do celular (API `ConsumerIrManager`) para mandar comandos SIRC
para a TV Sony, além de um Quick Settings Tile (`TvRemoteTileService`).

## Como abrir e rodar

1. Abra o **IntelliJ / Android Studio** → *Open* → selecione a pasta `SonyRemote`.
2. Espere o *Gradle sync* terminar (ele baixa o que precisa na primeira vez).
   - Precisa de **JDK 17** (o AGP 8.5 exige). Se der erro de Java, aponte para um JDK 17
     em *Settings → Build Tools → Gradle → Gradle JDK*.
3. Ligue o celular no cabo com **depuração USB** ativada.
4. Clique em **Run ▶** e escolha seu aparelho.

> Se aparecer aviso pedindo o `gradle-wrapper.jar`, deixe a IDE gerar o wrapper
> (ela faz isso sozinha no sync) — não incluí o `.jar` binário de propósito.

## Estrutura

- `MainActivity.kt` — monta a tela e liga cada botão a um código.
- `SircTransmitter.kt` — transforma (comando + endereço) em pulsos de IR.
- `SonyCodes.kt` — a tabela de códigos. **É aqui que você mexe.**
- `TvRemoteTileService.kt` — tile de acesso rápido nas configurações do Android.

## Se algum botão não funcionar

Os de power e volume são verificados. Os de navegação são o conjunto padrão
Sony e devem funcionar, mas se algum não responder:

1. Abra `SonyCodes.kt`.
2. Troque o número em hexadecimal do botão problemático.
3. Recompile e teste.

O endereço da TV é `1`. Todos os comandos usam o protocolo SIRC de 12 bits a 40 kHz.

## Ideias para depois

- Botões de números (0–9) para trocar de canal direto.
- Segurar o botão para repetir (volume contínuo).
- Salvar a última entrada usada.
