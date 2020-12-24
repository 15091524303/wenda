package com.nowcoder.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

/**AspectJ：
 *      Java社区里最完整最流行的AOP框架
 *      AspectJ支持5种类型的通知注解：
 *      @Before,@After,@AfterRunning,@AfterThrowing,@Around
 *      在AspectJ中，切入点表达式可以通过 “&&”、“||”、“!”等操作符结合起来。
 */
@Aspect   //在AspectJ注解中，切面只是一个带有@Aspect注解的Java类，它往往要包含很多通知。
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("execution(* com.nowcoder.controller.*Controller.*(..))")  //切入点表达式：作用于所有Controller类中声明的所有方法
    public void beforeMethod(JoinPoint joinPoint) {
        StringBuilder sb = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            if (arg != null) {
                sb.append("arg:").append(arg.toString()).append("|");
            }
        }
        logger.info("before method:" + sb.toString());
    }

    @After("execution(* com.nowcoder.controller.IndexController.*(..))")  //切入点表达式：作用于IndexController类中声明的所有方法
    public void afterMethod() {
        logger.info("after method" + new Date());
    }
}
