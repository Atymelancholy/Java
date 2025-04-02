package com.example.bookblog.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.bookblog.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Вход в метод контроллера: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.bookblog.controller.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("Выход из метода контроллера: {} с результатом: {}",
                    joinPoint.getSignature().toShortString(), result);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.example.bookblog.controller.*.*(..))",
            throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.error("Ошибка в методе контроллера: {} с причиной: {}",
                    joinPoint.getSignature().toShortString(), error.getMessage());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.example.bookblog.service.*.findById(..))",
            throwing = "error")
    public void logNotFound(JoinPoint joinPoint, Throwable error) {
        if (logger.isWarnEnabled()) {
            logger.warn("Объект не найден: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.example.bookblog.service.*.delete(..))",
            throwing = "error")
    public void logDeleteNotFound(JoinPoint joinPoint, Throwable error) {
        if (logger.isWarnEnabled()) {
            logger.warn("Попытка удалить несуществующий объект: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterThrowing(pointcut = "execution(* com.example.bookblog.service.*.save(..))",
            throwing = "error")
    public void logDuplicateSave(JoinPoint joinPoint, Throwable error) {
        if (logger.isWarnEnabled()) {
            logger.warn("Попытка создать дубликат объекта: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @Before("execution(* com.example.bookblog.cache.*.*(..))")
    public void logCacheOperations(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Операция с кэшем: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.bookblog.cache.InMemoryCache.put(..))")
    public void logCachePut(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Добавление в кэш: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.bookblog.cache.InMemoryCache.remove(..))")
    public void logCacheRemove(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Удаление из кэша: {} с аргументами: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.bookblog.cache.InMemoryCache.get(..))",
            returning = "result")
    public void logCacheGet(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("Чтение из кэша: {} с аргументами: {}, результат: {}",
                    joinPoint.getSignature().toShortString(), joinPoint.getArgs(), result);
        }
    }

    @AfterReturning(pointcut = "execution(* com.example.bookblog.cache.InMemoryCache.clear(..))")
    public void logCacheClear(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Очистка кэша: {}", joinPoint.getSignature().toShortString());
        }
    }
}
