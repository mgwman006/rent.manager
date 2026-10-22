package tz.tante.rent.manager.services.redis;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheDemoService {

  @Cacheable(value = "demo-cache", key = "#id")
  public String getMessage(Long id) {
    System.out.println("Executing method for id: " + id);
    return "Cached message for " + id;
  }

  @CacheEvict(value = "demo-cache", key = "#id")
  public void evictMessage(Long id) {
    System.out.println("Evicting cache for id: " + id);
  }
}
