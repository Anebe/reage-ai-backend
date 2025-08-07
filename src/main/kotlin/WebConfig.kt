import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

//@Configuration
//class WebConfig : WebMvcConfigurer {
//    override fun addCorsMappings(registry: CorsRegistry) {
//        registry.addMapping("/api/**") // Aplica CORS para todas as rotas que começam com /api
//            .allowedOrigins("http://localhost:3000") // A ORIGEM DO SEU FRONTEND NEXT.JS
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos para suas APIs
//            .allowedHeaders("*") // Permite todos os cabeçalhos (headers como Authorization)
//            .allowCredentials(true) // Se você usar cookies (não diretamente para JWT em localStorage, mas boa prática)
//            .maxAge(3600) // Tempo em segundos que o navegador pode cachear a resposta do preflight
//    }
//}