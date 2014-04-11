
-- 1 将系统表的主键字段，都改为  ID （40）
alter table sys_account change accountid id varchar(40);
alter table ar_collection change COLLECTIONID id varchar(40);
alter table ar_release change RELEASEID id varchar(40);
alter table sys_account_org change ACCOUNT_ORG_ID id varchar(40);
alter table sys_account_role change ACCOUNT_ROLE_ID id varchar(40);
alter table sys_account_tree change ACCOUNT_TREE_ID id varchar(40);
alter table sys_code change CODEID id varchar(40);
alter table sys_config change CONFIGID id varchar(40);
alter table sys_doc change DOCID id varchar(40);
alter table sys_docserver change DOCSERVERID id varchar(40);
alter table sys_errorlog change ERRORLOGID id varchar(40);
alter table sys_function change FUNCTIONID id varchar(40);
alter table sys_loginlog change LOGINLOGID id varchar(40);
alter table sys_operatelog change OPERATELOGID id varchar(40);
alter table sys_org change ORGID id varchar(40);
alter table sys_org_role change ORG_ROLE_ID id varchar(40);
alter table sys_org_tree change ORG_TREE_ID id varchar(40);
alter table sys_role change ROLEID id varchar(40);
alter table sys_role_function change ROLE_FUNCTION_ID id varchar(40);
alter table sys_table change TABLEID id varchar(40);

alter table sys_templet change TEMPLETID id varchar(40);
alter table sys_templetfield change TEMPLETFIELDID id varchar(40);
alter table sys_tree change TREEID id varchar(40);
alter table sys_tree_templet change TREE_TEMPLET_ID id varchar(40);

-- 2 修改sys_account表。增加orgid、roleid字段
alter table sys_account add orgid varchar(40), add roleid varchar(40);
-- 2 批量将帐户与组、角色重新对应
update sys_account a set a.orgid = (select o.orgid from sys_account_org o where a.id = o.accountid);
update sys_account a set a.roleid = (select r.roleid from sys_account_role r where a.id = r.accountid); 
-- 3 删除帐户与组、角色对应表
DROP TABLE IF EXISTS sys_account_org;
DROP TABLE IF EXISTS sys_account_role;


-- 4 修改sys_org表。增加roleid字段
alter table sys_org add roleid varchar(40);
-- 4 批量将组与角色重新对应
update sys_org o set o.roleid = (select r.roleid from sys_org_role r where o.id = r.orgid); 
-- 4 删除帐户与组、角色对应表
DROP TABLE IF EXISTS sys_org_role;


-- 5 修改sys_tree表。增加templetid字段
alter table sys_tree add templetid varchar(40);
-- 5 批量将树与档案类型重新对应
update sys_tree t set t.templetid = (select m.templetid from sys_tree_templet m where t.id = m.treeid); 
-- 5 删除帐户与组、角色对应表
DROP TABLE IF EXISTS sys_tree_templet;

-- 6 删除sys_doc表多余字段
alter table sys_doc drop column PARENTID;
alter table sys_doc drop column MTIME;
alter table sys_doc drop column LOCKED;
alter table sys_doc drop column HEIGHT;
alter table sys_doc drop column HIDDEN;
alter table sys_doc drop column MIME;
alter table sys_doc drop column WIDTH;
alter table sys_doc drop column MREAD;
alter table sys_doc drop column MWRITE;
alter table sys_doc drop column AUTHORITY;

-- 7 在角色表增加orgid
alter table sys_role add orgid varchar(40);
-- 7 在档案类型表增加(orgid)
alter table sys_templet add orgid varchar(40)
-- 7 在组表增加字段(未做sql)
--alter table sys_org add orgid varchar(40)
-- 8 在sys_account增加filter字段（text），数据访问权限
alter table sys_account_tree add filter text
-- 8 在sys_org增加filter字段（text），数据访问权限
alter table sys_org_tree add filter text

-- 9 创建表sys_init
CREATE TABLE IF NOT EXISTS sys_init (
  id varchar(40) NOT NULL DEFAULT '',
  initkey varchar(200) ,
  initkeymemo varchar(100),
  initvalue varchar(500) ,
  PRIMARY KEY (id)
);

--10 收藏夹表增加帐户id
alter table ar_collection add accountid varchar(40);


--11 创建sys_rolegroup表，作为角色组表(取消)
--CREATE TABLE IF NOT EXISTS sys_rolegroup (
--  id varchar(40) NOT NULL DEFAULT '',
--  groupname varchar(200) ,
--  parentid varchar(100),
--  groupsort int ,
--  ownerids text,
--  ownername text,
--  PRIMARY KEY (id)
--);

--11 插入sys_rolegroup初始化第一级节点
--insert into sys_rolegroup (id,groupname,parentid,groupsort) value ('1','角色组','0',1);

--11 在role表增加groupid，角色与角色组的关联
--alter table sys_role add groupid varchar(40);

--12在表sys_org_tree\sys_account_tree\sys_doc 增加字段docauth
alter table sys_org_tree add docauth varchar(40);
alter table sys_account_tree add docauth varchar(40);
alter table sys_doc add docauth varchar(40);

--13在表sys_templet 增加sort字段（int 11）字段，用来档案类型排序
alter table sys_templet add sort int(11) default 0;

--14在表sys_templetfield 增加orderby （varchar 30）  iscopy（int 11）［1:添加字段内容时，继承自上一条记录 0:不顺带］
alter table sys_templetfield add orderby varchar(30);
alter table sys_templetfield add iscopy int(11) default 0;

--15在表sys_tree 增加sort字段（int 11）字段，用来档案类型排序
alter table sys_tree add sort int(11) default 0;
--首次时，更新tree表sort，按照templet表的sort
update sys_tree r ,sys_templet t set r.sort = t.sort where r.templetid = t.id  and r.treetype <> 'W'

--16在表sys_templet 增加parentid（varchar 40） 档案库也可以增加档案夹，需要parentid画树
alter table sys_templet add parentid varchar(40);


--17 在表sys_config表增加accountid，每个帐户可以自己设定配置（每页显示行数、列表字符截取数）.accountid＝“SYSTEM”表示系统配置
alter table sys_config add accountid varchar(40);
update sys_config set accountid = 'SYSTEM'

--18 表sys_templetfield表增加accountid，每个帐户可以自己设定字段（仅限字段的排序、gird显示）.accountid＝“SYSTEM”表示系统配置字段
alter table sys_templetfield  add accountid varchar(40);
update sys_templetfield set accountid = 'SYSTEM'

--19、在表sys_doc表增加createrid（varchar 40） 电子文件上传人的帐户id
alter table sys_doc  add createrid varchar(40);



