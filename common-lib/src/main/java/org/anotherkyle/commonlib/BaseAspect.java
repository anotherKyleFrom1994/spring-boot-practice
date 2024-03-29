package org.anotherkyle.commonlib;

import lombok.NonNull;
import org.anotherkyle.commonlib.exception.ApplicationException;
import org.anotherkyle.commonlib.util.MessageUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public abstract class BaseAspect {
    protected final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @NonNull
    protected static <T> T getFromJointPointArgs(Object[] args, Class<T> type) {
        return Arrays.stream(args)
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElseThrow(() -> new ApplicationException(String.format("No type%sin args", type.getName()), ApplicationStatus.INPUT_VALIDATE_FAILED));
    }

    protected static String truncateMsg(@NonNull String msg) {
        return MessageUtil.truncateMsg(msg, 1000, 500);
    }

    protected <T> Publisher<T> processMethod(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        Class<?> returnType = ((MethodSignature) signature).getReturnType();

        if (returnType.isAssignableFrom(Mono.class))
            return Mono.just(joinPoint)
                    .flatMap(jp -> {
                        try {
                            return (Mono<T>) jp.proceed();
                        } catch (Throwable e) {
                            return Mono.error(new ApplicationException(e));
                        }
                    });
        else
            return Mono.just(joinPoint)
                    .flatMapMany(jp -> {
                        try {
                            return ((Flux<T>) jp.proceed());
                        } catch (Throwable e) {
                            return Flux.error(new ApplicationException(e));
                        }
                    });
    }
}
