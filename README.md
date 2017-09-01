# java.wechat.pay.service
微信支付服务demo

数据库SQL：
CREATE TABLE `pay_server_log` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `order_id` varchar(32) NOT NULL COMMENT '订单id（自己商户系统的订单id）',
  `third_order_id` varchar(32) NOT NULL COMMENT '第三方支付异步回调返回的订单id',
  `third_platform` varchar(32) NOT NULL COMMENT '第三方支付平台名称',
  `total_fee` decimal(10,2) NOT NULL COMMENT '总支付金额',
  `description` text COMMENT '系统调用此服务的描述',
  `openid` varchar(100) DEFAULT NULL COMMENT '微信用户的open id（仅微信用户会有）', 
  `third_pre_pay_data` text COMMENT '第三方预支付订单信息',
  `third_notify_data` text COMMENT '第三方支付返回的字符串',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间（此条数据创建的时间）',
  PRIMARY KEY (`id`,`order_id`,`third_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付服务日志表';

CREATE TABLE `request_log` (
  `order_id` varchar(32) NOT NULL COMMENT '订单id',
  `client_ip` varchar(20) NOT NULL COMMENT '请求ip',
  `request_type` varchar(10) NOT NULL COMMENT 'url请求类型请求类型',
  `request_url` text NOT NULL COMMENT '请求url',
  `request_parameters` text COMMENT '请求参数',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据写入时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付服务请求日志表';

项目download之后， 
第一步：先在WXPayConstants文件中，填入app id，商户id， 商户key以及在微信商户平台内设置的key
PS:微信商户平台key的获取：【账户中心】--【账户设置】--【API安全】--【API密钥】中设置的key
第二步：填写context.xml中的数据库配置。如果要部署到服务器，将context.xml复制到tomcat/conf下替换元来的context.xml文件，重启tomcat。

测试用例在test路径下的 getQRCodeServletTest