package subha.app.cyra

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform