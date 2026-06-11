# KaraokeApp 🎤

Um aplicativo de Karaokê nativo para Android, construído com Kotlin e Jetpack Compose. Inspirado no projeto open source [UltraStar Deluxe](https://github.com/UltraStar-Deluxe/USDX) e no [UltraStar Play](https://github.com/UltraStar-Deluxe/Play), com arquitetura moderna e Material Design 3.

---

## Funcionalidades

| Funcionalidade | Detalhe |
|---|---|
| **Biblioteca local** | Escaneia músicas MP3/AAC do armazenamento do dispositivo |
| **Letras sincronizadas** | Integração com [LRCLib](https://lrclib.net) para buscar letras LRC gratuitas |
| **Player de Karaokê** | Letras animadas com destaque da linha atual (auto-scroll) |
| **Detecção de tom** | Captura do microfone com algoritmo YIN simplificado |
| **Pontuação** | Cálculo de score baseado em afinação e ritmo com grade visual |
| **Download grátis** | Músicas sob licença Creative Commons via [Jamendo](https://developer.jamendo.com) |
| **Favoritos e busca** | Gerenciamento da biblioteca com busca e filtros |
| **Configurações** | Fonte, ganho do microfone, sensibilidade, tema escuro/dinâmico |

---

## Capturas de Tela (UI)

```
Home ──► Biblioteca ──► Player (karaokê + score)
                └──► Download (Jamendo)
                └──► Configurações
```

---

## Tecnologias

### Android / Kotlin
- **Kotlin 2.1** + Coroutines + Flow
- **Jetpack Compose** com Material Design 3
- **Navigation Compose** para roteamento
- **Hilt** (Dagger) para injeção de dependência

### Mídia
- **Media3 / ExoPlayer 1.5.1** para playback de áudio
- **MediaSession** para controles na tela de bloqueio e notificações

### Dados
- **Room 2.6** para banco de dados local (songs + lyrics cache)
- **DataStore Preferences** para configurações
- **Retrofit + OkHttp** para chamadas de API

### APIs externas (gratuitas, sem chave de API)
- **LRCLib** (`https://lrclib.net`) — letras sincronizadas em formato LRC
- **Jamendo** (`https://api.jamendo.com`) — músicas Creative Commons para download

---

## Arquitetura

```
app/
├── data/
│   ├── local/        # Room DB (SongEntity, LyricsEntity, DAOs)
│   ├── remote/       # APIs Retrofit (LrcLibApi, JamendoApi) + DTOs
│   └── repository/   # Implementações dos repositórios
├── domain/
│   ├── model/        # Song, LyricLine, SongScore, DownloadableSong
│   ├── repository/   # Interfaces dos repositórios
│   └── usecase/      # Casos de uso (GetLyrics, CalculateScore, etc.)
├── presentation/
│   ├── navigation/   # NavHost + bottom navigation
│   ├── theme/        # Material 3 colors, typography, theme
│   ├── ui/
│   │   ├── home/     # HomeScreen + HomeViewModel
│   │   ├── library/  # LibraryScreen + LibraryViewModel
│   │   ├── player/   # PlayerScreen + PlayerViewModel + components
│   │   ├── download/ # DownloadScreen + DownloadViewModel
│   │   └── settings/ # SettingsScreen + SettingsViewModel
│   └── components/   # SongCard compartilhado
├── di/               # Hilt modules (App, Database, Network)
├── service/          # KaraokePlaybackService (MediaSession)
└── util/             # LrcParser, PitchDetector, Extensions
```

Padrão: **MVVM + Clean Architecture** com separação em camadas domain/data/presentation.

---

## Como compilar

### Pré-requisitos
- Android Studio Ladybug (2024.2) ou superior
- JDK 17+
- Android SDK 35 (target), mín. SDK 26 (Android 8.0)

### Passos

```bash
# Clone o repositório
git clone https://github.com/<seu-usuario>/KaraokeApp.git
cd KaraokeApp

# Compile via Gradle
./gradlew assembleDebug

# Instale no dispositivo conectado
./gradlew installDebug
```

### Variáveis de build
O app usa o **client_id público do Jamendo** (`b6747d04`). Para produção, registre seu próprio app em [developer.jamendo.com](https://developer.jamendo.com) e substitua a constante em `JamendoApi.kt`.

---

## Formatos de letra suportados

O parser `LrcParser.kt` suporta:
- **LRC padrão**: `[mm:ss.xx]texto da linha`
- **Enhanced LRC**: timestamps por palavra `<mm:ss.xx>palavra`
- Tags de metadados (`[ti:]`, `[ar:]`, etc.) são ignoradas automaticamente

---

## Comparação com UltraStar Deluxe

| | KaraokeApp | UltraStar Deluxe | UltraStar Play |
|---|---|---|---|
| Plataforma | Android nativo | Windows/Linux/macOS | Desktop + Mobile (Unity) |
| Linguagem | Kotlin | Pascal | C# (Unity) |
| Letras | LRC (via LRCLib) | .txt UltraStar | UltraStar format |
| Notas musicais | Detecção por pitch | Sim (completo) | Sim (completo) |
| Download grátis | Jamendo CC | Não | Demo songs CC |
| Open source | ✅ | ✅ GPL-2.0 | ✅ MIT |

---

## Licença

MIT — veja [LICENSE](LICENSE)

Músicas do Jamendo estão sob licença Creative Commons. Letras providas pelo LRCLib conforme termos de cada provedor.
