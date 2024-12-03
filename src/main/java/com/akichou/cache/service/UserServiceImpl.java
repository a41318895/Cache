package com.akichou.cache.service;

import com.akichou.cache.entity.User;
import com.akichou.cache.entity.dto.QueryUserDto;
import com.akichou.cache.entity.dto.UserDto;
import com.akichou.cache.entity.vo.UserVo;
import com.akichou.cache.mapper.UserMapper;
import com.akichou.cache.properties.CacheProperties;
import com.akichou.cache.repository.UserRepository;
import com.akichou.cache.util.LockManagerUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository ;
    private final RedisTemplate<String, Object> redisTemplate ;
    private final CacheProperties prop ;

    private LoadingCache<String, User> localCache ;

    @PostConstruct
    public void initLocalCache() {

        localCache = Caffeine.newBuilder()
                .maximumSize(prop.getMaxSize())
                .expireAfterWrite(prop.getExpirationMinutes(), TimeUnit.MINUTES)
                .recordStats()
                .removalListener((key, value, cause) -> log.info("KEY: {} Removed. Because: [ {} ]", key, cause))
                .build(this::queryFromRedis) ;

        scheduleStatsPrinting() ;
    }

    @Override
    public ResponseEntity<UserVo> getUserById(QueryUserDto queryUserDto) {

        String targetId = queryUserDto.id() ;

        // Get User from localCache, if null then get from redis
        User cacheUser = localCache.get("user:" + targetId) ;

        if (cacheUser == null) {

            Object lock = LockManagerUtil.getLock(targetId) ;

            return queryFromDatabaseWithLocking(targetId, lock) ;
        }

        log.info("Found user entity from cache: [ ID = {} ]", targetId) ;

        return ResponseEntity.ok(UserMapper.mapUserToUserVo(cacheUser)) ;
    }

    @Override
    public ResponseEntity<UserVo> addUser(UserDto userDto) {

        User user = UserMapper.mapUserDtoToUser(userDto) ;

        User savedUser = userRepository.save(user) ;

        UserVo userVo = UserMapper.mapUserToUserVo(savedUser) ;

        return ResponseEntity.ok(userVo) ;
    }

    private User queryFromRedis(String id) {

        return (User) redisTemplate.opsForValue().get("user:" + id) ;
    }

    private ResponseEntity<UserVo> queryFromDatabaseWithLocking(String targetId, Object lock) {

        // Avoid Hotspot Invalid Problem
        synchronized (Objects.requireNonNull(lock)) {

            // Check again to prevent previous thread has set queried value into localCache and redis
            User cacheUser = localCache.get("user:" + targetId) ;

            if (cacheUser == null) {

                // Avoid Cache Penetration Problem ( Set notfound user null )
                User queriedUser = userRepository.findById(UUID.fromString(targetId)).orElse(null) ;

                handleCaching(targetId, queriedUser) ;

                log.info("Found user entity from database and stored into cache: [ ID = {} ]", targetId) ;

                return ResponseEntity.ok(UserMapper.mapUserToUserVo(queriedUser)) ;
            }

            log.info("Found user entity from cache in synchronized zone: [ ID = {} ]", targetId) ;

            return ResponseEntity.ok(UserMapper.mapUserToUserVo(cacheUser)) ;
        }
    }

    private void handleCaching(String targetId, User queriedUser) {

        if (queriedUser == null) {

            redisTemplate.opsForValue().set("user:" + targetId, NullValue.INSTANCE,
                                             prop.getRedisNullTimeout(), TimeUnit.MINUTES) ;

            localCache.put("user:" + targetId, null) ;
        }

        int randomMinuteNumber = (int) (Math.random() * prop.getRandomRate()) + 1 ;   // Avoid Cache Avalanche Problem

        redisTemplate.opsForValue().set("user:" + targetId, queriedUser,
                prop.getRedisTimeout() + randomMinuteNumber, TimeUnit.MINUTES) ;
        log.info("Write user into redis cache: [ ID = {} ]", targetId) ;

        localCache.put("user:" + targetId, queriedUser) ;
        log.info("Write user into local cache: [ ID = {} ]", targetId) ;
    }

    private void scheduleStatsPrinting() {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor() ;

        scheduler.scheduleAtFixedRate(() -> {

            CacheStats stats = localCache.stats() ;

            log.info("Cache Stats - Hit Rate: {}, Eviction Count: {}, Load Count: {}",
                    stats.hitRate(), stats.evictionCount(), stats.loadCount()) ;

        }, prop.getStatusPrintDelayInit(), prop.getStatusPrintPeriod(), TimeUnit.MINUTES) ;
    }
}
