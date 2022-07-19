package yeshenko.edge.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static yeshenko.edge.constant.Constants.AUTHORIZATION_HEADER;
import static yeshenko.edge.constant.Constants.SCHEMA_REQUIREMENT;
import static yeshenko.edge.constant.Constants.SCHEME_OAUTH;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api(@Value("${swagger-ui.config.open-id-connect-uri}") String url) {
        return new OpenAPI()
                .info(new Info()
                        .title("Edge Service API")
                        .description("Edge service with backend API call to Core service")
                        .version("v0.1.0"))
                .schemaRequirement(SCHEMA_REQUIREMENT, new SecurityScheme()
                        .openIdConnectUrl(url)
                        .type(SecurityScheme.Type.OPENIDCONNECT)
                        .name(AUTHORIZATION_HEADER)
                        .scheme(SCHEME_OAUTH)
                        .in(SecurityScheme.In.HEADER)
                        .flows(new OAuthFlows()));
    }


}