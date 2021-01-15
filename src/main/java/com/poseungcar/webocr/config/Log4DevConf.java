package com.poseungcar.webocr.config;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@Aspect
@Profile("dev")
public class Log4DevConf {

    @Around("execution(* com.poseungcar.webocr.*.*Controller.*(..)))") // ex. within(me.shinsunyoung.demo..*)) 1
    public Object logging(ProceedingJoinPoint pjp) throws Throwable { // 2
  
      String params = getRequestParams(); // request 값 가져오기
  
      long startAt = System.currentTimeMillis();

      StringBuilder logMsg = new StringBuilder();
      logMsg.append("###################\n");
      logMsg.append(String.format("-----------> REQUEST : {}({}) = {}", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName(), params));
      logMsg.append("###################\n");
  
      Object result = pjp.proceed(); // 4
  
      long endAt = System.currentTimeMillis();

      logMsg.append(String.format("-----------> RESPONSE : {}({}) = {} ({}ms)", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName(), result, endAt - startAt));

      log.info(logMsg.toString());
      return result;
    }

    private String paramMapToString(Map<String, String[]> paramMap) {
        return paramMap.entrySet().stream()
            .map(entry -> String.format("%s -> (%s)",
                entry.getKey(), Joiner.on(",").join(entry.getValue())))
            .collect(Collectors.joining(", "));
    }
  
    // Get request values
    private String getRequestParams() {
  
      String params = "없음";
  
      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes(); // 3
  
      if (requestAttributes != null) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest(); 
  
        Map<String, String[]> paramMap = request.getParameterMap();
        if (!paramMap.isEmpty()) {
          params = " [" + paramMapToString(paramMap) + "]";
        }
      }
      return params;
    }
}