package bank.dev.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggerThrows {

    @AfterThrowing(value = "execution(* bank.dev.service.AccountService.*(..))", throwing = "exc")
    public void afterThrowingAccountService(JoinPoint joinPoint, Throwable exc) {
        System.out.println(exc.getMessage());
    }

    @AfterThrowing(value = "execution(* bank.dev.service.UserService.*(..))", throwing = "exc")
    public void afterThrowingUserService(JoinPoint joinPoint, Throwable exc) {
        System.out.println(exc.getMessage());
    }

}
