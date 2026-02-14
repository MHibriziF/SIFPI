package id.go.kemenkoinfra.ipfo.sifpi.common.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import id.go.kemenkoinfra.ipfo.sifpi.common.constants.AppConstant;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();

        // Projects — updated quarterly, cache aggressively
        manager.registerCustomCache(AppConstant.CACHE_PROJECTS,
            Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(Duration.ofDays(7))
                .recordStats()
                .build());

        // News — updated frequently
        manager.registerCustomCache(AppConstant.CACHE_NEWS,
            Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(Duration.ofHours(1))
                .recordStats()
                .build());

        // Project detail — individual project pages
        manager.registerCustomCache(AppConstant.CACHE_PROJECT_DETAIL,
            Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofDays(1))
                .recordStats()
                .build());

        // Default for any other cache names
        manager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(10))
            .recordStats());

        return manager;
    }
}