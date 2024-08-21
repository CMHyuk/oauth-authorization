package com.oauth.authorization.global.log;

import org.aspectj.lang.annotation.Pointcut;

public class TimeLogPointCut {

    @Pointcut("domainPackagePointCut() && ( componentPointCut() || servicePointCut() || repositoryPointCut() )")
    public void timeLogPointCut() {}

    @Pointcut("execution(* com.oauth.authorization.domain..*.*(..))")
    private void domainPackagePointCut() {}

    @Pointcut("@target(org.springframework.stereotype.Service)")
    private void servicePointCut() {}

    @Pointcut("@target(org.springframework.stereotype.Repository)")
    private void repositoryPointCut() {}

    @Pointcut("@target(org.springframework.stereotype.Component)")
    private void componentPointCut() {}
}
