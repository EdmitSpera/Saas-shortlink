# Saas-shortlink
### 生成短链流程图
![processing.png](processing.png)

![processing_.png](processing_.png)

## 项目技术回顾
### Spring-boot项目的创建和配置过程
- 依赖
    - 父依赖spring-boot-starter-web规约版本号
    - 子模块依赖 不需要声明版本，继承父依赖
- 配置
    - 端口号配置
    - 数据源配置
    - 分表配置
### apiFox接口文档使用
- 运行环境管理
    - 服务url
    - 全局环境变量
- 接口文档声明
- 调试接口
### 编码api的过程
- Dao持久层实体声明
    - entity
    - mapper
    - 部分字段自动填充功能Mybatis-plus
        - Handle类 继承MetaObjectHandler接口，重写insertFill和updateFill方法
        - 注解标识实体字段@TableField(fill = FieldFill.INSERT_UPDATE)
- DTO数据传输对象声明
    - req
    - resp
- Controller控制层
- Service服务层
    - service interface
    - service impl
- Filter
    - 实现过滤器逻辑
        - 继承Filter接口
        - 重写doFilter逻辑
            - 放行接口 -> 后续使用网关，可忽略
            - 验证登录
        - 调用filterChain.doFilter()放行
    - 配置Filter
        - 注册过滤器
        - 过滤规则
        - 执行顺序
### 全局设置
- 全局错误响应码设计errorcode
    - IErrorCode《interface》
    - impl
- 全局异常设计exception
    - 抽象异常 -> 封装响应码和响应信息
    - 客户端异常
    - 服务端异常
    - 远程调用服务异常
- 全局返回对象设计result
    - Result
        - code
        - message
        - data -> dto
        - requestId
        - success
    - Results 构造者模式 模板方法
### 敏感信息脱敏
- Json序列化返回脱敏
- 敏感信息加密存储 ShardingSphere
### 布隆过滤器应用——穿透场景
- 依赖引入
- 配置Redis
- 配置类
    - 元素个数
    - 误判率
### ShardingSphere分表
- 引依赖
- 配置
    - application.yaml配置ShardingSphere数据源
    - shardingsphere-config.yaml配置
        - 分片键
        - 分片算法
- DDL分表
### 用户上下文——阿里巴巴TTL
```sql
// 阿里巴巴开源的TTL 线程安全的ThreadLocal 能够实现没有任何关系的类间跨线程的传递
// 意味着即便在多线程的情况下也能获取到线程信息
private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();
```
### 分页功能——Mybatis-Plus
- 配置分页插件
- 声明分页查询Param类 -> 继承Page<Object>

出于**交流学习**目的，非商业用途，侵删
参考资料
https://nageoffer.com/shortlink/#%E9%A1%B9%E7%9B%AE%E6%8F%8F%E8%BF%B0
https://baomidou.com/introduce/
https://shardingsphere.apache.org/document/current/cn/overview/