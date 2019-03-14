SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE IF EXISTS sys_region;




/* Create Tables */

-- 行政区表
CREATE TABLE sys_region
(
	-- 主键
	id varchar(32) NOT NULL COMMENT '主键',
	-- 编码
	code varchar(18) NOT NULL COMMENT '编码',
	-- 名称
	name varchar(64) NOT NULL COMMENT '名称',
	-- 类型
	type varchar(16) NOT NULL COMMENT '类型',
	-- 等级
	level int(1) NOT NULL COMMENT '等级',
	-- 父级
	parent_id varchar(32) COMMENT '父级',
	-- 所属国家
	nation_id varchar(32) COMMENT '所属国家',
	-- 时间戳
	coru_time timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '时间戳',
	PRIMARY KEY (id)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8  COMMENT = '行政区表';



