# mygtw
### 概述
  工作中要用到 gateway,不熟悉 cloud,参考很多他人代码.整理出 spring cloud gateway 的一些代码,已满足项目需求.  
  
  gateway 作为网关,实际也就是个 filter,负责对请求进行鉴权认证保护,保护后端服务不对外暴露.
### 项目结构
  mygtw 目前三个module  
  >common-->公共服务.   
  >公共响应类,响应码和对应的信息  
  
  >gtw-->网关.
  > 获取、修改 request(header,body) 的filter
  > 获取、修改 response(header,body)的 filter  
  
 > account-->账户服务
 > 账户服务若干接口(数据暂时写死) ,不对外提供接口,只接受 gtw 转发来的请求.

#### 部分filter 说明
 > * gtw下面/config/apiLocator, 是读取 requestbody 的 java dsl 配置.  
 >> 自定义router中配置了ReadBodyPredicateFactory，然后在filter中通过cachedRequestBodyObject缓存字段获取request body信息，这种解决，一不会带来重复读取问题，二不会带来requestbody取不全问题。三在低版本的Spring Cloud Finchley.SR2也可以运行。
  
 > * responseBodyModifyFilter 响应值传回请求方时,可查看、修改 response 中的信息(body、header).  
 >> filter 获取到 原始response,通过 response 的包装类(ServerHttpResponseDecorator)去取出相应信息. ServerHttpResponseDecorator的内部类把 responsebody 从 bufferFactory读出,对该信息进行操作后,把需要的信息重新转成 byte[]写回 bufferFactory 中,交给 filter,filter 之后会将该信息用 netty 写回给客户端.
 
  ``` java
  //obj 就是 request body 信息
Object obj = exchange.getAttribute("cachedRequestBodyObject");

  //取 request header 中的 XXX 值
String xxx= exchange.getRequest().getHeaders().getFirst("XXX");

 //把 reqTs 塞入 request header 中
ServerHttpRequest reqTs = exchange.getRequest().mutate().header("reqTs", "21321").build();
 //将现在的request 变成 change对象
ServerWebExchange build = exchange.mutate().request(reqTs).build();

```

##### 大坑小坑都是坑
 * springboot 项目一般都依赖 spring web starter,也即是用内置的 tomcat 容易.gateway 默认用的 netty 服务器,请求和响应不是 httpRequest、httpServletRequest(这种是javax.servle的),ServerHttpRequest(Spring 5.0).
 如果一个module 中有牵涉到 web 和 netty 的时候,启动失败,报错如下, 剔除 web 依赖即可

 ```
Spring NVC found on classpath, which is incompatible with Spring Cloud Gateway at this time. Please remove spr ing-boot- -starter- web dependency.
```
* gateway 的filter 可以设置过滤链表顺序设置,数值越小越先执行.
* gateway 的 filter 分为 gatewayFilter 和 globalFilter. gatewayFilter是本质和 web 的 filter 类似.
* 配置文件 注意空格和缩进,小问题的坑掉进去几小时老冤了
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: first
          uri: http://localhost:8091
          predicates:
            - Path=/account/index/**
```

###### 参考
[获取 requestBody](https://blog.51cto.com/thinklili/2329184?cid=725051)  
[修改 responseBody](https://github.com/spring-cloud/spring-cloud-gateway/issues/47)
 

 
  