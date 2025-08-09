package gr.hua.dit.ds.springbootdemo.config;

import gr.hua.dit.ds.springbootdemo.entity.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config, CorsRegistry cors) {
        // Expose entity IDs in REST responses
        config.exposeIdsFor(Project.class);
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Role.class);
        config.exposeIdsFor(Pledge.class);
    }
}