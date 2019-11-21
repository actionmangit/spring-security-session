# spring-security-session

## migration

- security.ignored
  - Spring boot 1.X 버전에서만 사용가능
  - 2.X 에서는 자바 세팅만 가능

    ~~~java
    @Override
    public void configure(WebSecurity web) {
        web
            .ignoring()
                .antMatchers("/*es2015.*");
    }
    ~~~

    - `permitAll()`을 사용해도 좋으나 정적 리소스는 `ignoring()` 하는것이 좋다. (`permitAll()`의 경우 필터 동작이 추가됨)

- Spring Session 및 redis 종속성 변경
  - AS IS

    ~~~xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    ~~~

  - TO BE

    ~~~xml
    <dependency>
        <groupId>org.springframework.session</groupId>
        <artifactId>spring-session-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>io.lettuce</groupId>
        <artifactId>lettuce-core</artifactId>
        <version>5.2.0.RELEASE</version>
    </dependency>
    ~~~

    - 2.X로 넘어오면서 redis 클라이언트를 설정할 수 있게 되었는데 (Jedis, Lettuce) Lettuce 가 Asyc 방식으로 퍼포먼스 면에서 더 좋은 선택이다.

- `@EnableRedisHttpSession` 사용시 `spring.session.store-type: redis` 제거 필요.

- `HeaderHttpSessionStrategy` 삭제

  - AS IS

    ~~~java
    @Bean
    HeaderHttpSessionStrategy sessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }
    ~~~

  - TO BE

    ~~~java
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }
    ~~~

- security.sessions
  - Spring boot 1.X 버전에서만 사용가능
  - 2.X 에서는 자바 세팅만 가능

  ~~~java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
            .cors().and()
            .authorizeRequests()
                .anyRequest().authenticated();
    }
  ~~~

## 내용

- 인증 서버와 resource 서버 분할
- CORS 옵션 설정
- resource 서버 보안 설정
  - 토큰 인증을 위해 ui 서버의 세션 아이디를 토큰에 저장
  - resource 서버의 security 설정에 cors 옵션을 추가함(permitAll()의 경우 프리플라이트때 민감한 데이터를 실수로 보낼수가 있어서 안전한 cors()사용)
  - resource 서버의 세션을 토큰상의 값으로 대체하고 확인하도록 로직 추가
- 왜 쿠키와 함께 동작하지 않았을까?
  - 파트2에서 복잡성을 높이지 않기 위해 쿠키와 세션을 사용한다고 했는데 쿠키를 사용하지 않고 왜 복잡하게 구현했을까?
  - HttpOnly 때문에
  
