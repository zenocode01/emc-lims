-- EMC LIMS 数据库索引优化脚本
-- 执行日期: 2026-06-12
-- 说明: 为常用查询字段添加索引，提升数据库查询性能

-- ============================================
-- 系统管理模块索引
-- ============================================

-- 用户表索引
CREATE INDEX IF NOT EXISTS idx_sys_user_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_sys_user_phone ON sys_user(phone);
CREATE INDEX IF NOT EXISTS idx_sys_user_dept_id ON sys_user(dept_id);
CREATE INDEX IF NOT EXISTS idx_sys_user_status ON sys_user(status);
CREATE INDEX IF NOT EXISTS idx_sys_user_create_time ON sys_user(create_time DESC);

-- 角色表索引
CREATE INDEX IF NOT EXISTS idx_sys_role_role_code ON sys_role(role_code);
CREATE INDEX IF NOT EXISTS idx_sys_role_status ON sys_role(status);

-- 菜单表索引
CREATE INDEX IF NOT EXISTS idx_sys_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_menu_menu_type ON sys_menu(menu_type);
CREATE INDEX IF NOT EXISTS idx_sys_menu_path ON sys_menu(path);
CREATE INDEX IF NOT EXISTS idx_sys_menu_sort ON sys_menu(sort);

-- 用户角色关联索引
CREATE INDEX IF NOT EXISTS idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_sys_user_role_role_id ON sys_user_role(role_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_sys_user_role_unique ON sys_user_role(user_id, role_id);

-- 角色菜单关联索引
CREATE INDEX IF NOT EXISTS idx_sys_role_menu_role_id ON sys_role_menu(role_id);
CREATE INDEX IF NOT EXISTS idx_sys_role_menu_menu_id ON sys_role_menu(menu_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_sys_role_menu_unique ON sys_role_menu(role_id, menu_id);

-- 部门表索引
CREATE INDEX IF NOT EXISTS idx_sys_dept_parent_id ON sys_dept(parent_id);
CREATE INDEX IF NOT EXISTS idx_sys_dept_dept_type ON sys_dept(dept_type);
CREATE INDEX IF NOT EXISTS idx_sys_dept_status ON sys_dept(status);

-- ============================================
-- 客户管理模块索引
-- ============================================

-- 客户表索引
CREATE INDEX IF NOT EXISTS idx_customer_name ON customer(name);
CREATE INDEX IF NOT EXISTS idx_customer_type ON customer(type);
CREATE INDEX IF NOT EXISTS idx_customer_industry ON customer(industry);
CREATE INDEX IF NOT EXISTS idx_customer_status ON customer(status);
CREATE INDEX IF NOT EXISTS idx_customer_create_time ON customer(create_time DESC);

-- 联系人表索引
CREATE INDEX IF NOT EXISTS idx_customer_contact_customer_id ON customer_contact(customer_id);
CREATE INDEX IF NOT EXISTS idx_customer_contact_name ON customer_contact(name);
CREATE INDEX IF NOT EXISTS idx_customer_contact_phone ON customer_contact(phone);

-- ============================================
-- 样品管理模块索引
-- ============================================

-- 样品表索引
CREATE INDEX IF NOT EXISTS idx_sample_sample_no ON sample(sample_no);
CREATE INDEX IF NOT EXISTS idx_sample_customer_id ON sample(customer_id);
CREATE INDEX IF NOT EXISTS idx_sample_status ON sample(status);
CREATE INDEX IF NOT EXISTS idx_sample_receive_date ON sample(receive_date DESC);
CREATE INDEX IF NOT EXISTS idx_sample_product_name ON sample(product_name);
CREATE INDEX IF NOT EXISTS idx_sample_create_time ON sample(create_time DESC);

-- 样品日志表索引
CREATE INDEX IF NOT EXISTS idx_sample_log_sample_id ON sample_log(sample_id);
CREATE INDEX IF NOT EXISTS idx_sample_log_operate_time ON sample_log(operate_time DESC);
CREATE INDEX IF NOT EXISTS idx_sample_log_to_status ON sample_log(to_status);

-- ============================================
-- 测试管理模块索引
-- ============================================

-- 测试项目表索引
CREATE INDEX IF NOT EXISTS idx_test_item_code ON test_item(code);
CREATE INDEX IF NOT EXISTS idx_test_item_category ON test_item(category);
CREATE INDEX IF NOT EXISTS idx_test_item_status ON test_item(status);

-- 测试计划表索引
CREATE INDEX IF NOT EXISTS idx_test_plan_plan_no ON test_plan(plan_no);
CREATE INDEX IF NOT EXISTS idx_test_plan_sample_id ON test_plan(sample_id);
CREATE INDEX IF NOT EXISTS idx_test_plan_customer_id ON test_plan(customer_id);
CREATE INDEX IF NOT EXISTS idx_test_plan_status ON test_plan(status);
CREATE INDEX IF NOT EXISTS idx_test_plan_plan_date ON test_plan(plan_date DESC);

-- 测试记录表索引
CREATE INDEX IF NOT EXISTS idx_test_record_test_plan_id ON test_record(test_plan_id);
CREATE INDEX IF NOT EXISTS idx_test_record_test_item_id ON test_record(test_item_id);
CREATE INDEX IF NOT EXISTS idx_test_record_tester_id ON test_record(tester_id);
CREATE INDEX IF NOT EXISTS idx_test_record_result ON test_record(result);
CREATE INDEX IF NOT EXISTS idx_test_record_test_date ON test_record(test_date DESC);

-- 测试曲线表索引
CREATE INDEX IF NOT EXISTS idx_test_curve_test_record_id ON test_curve(test_record_id);

-- ============================================
-- 报告管理模块索引
-- ============================================

-- 报告表索引
CREATE INDEX IF NOT EXISTS idx_report_report_no ON report(report_no);
CREATE INDEX IF NOT EXISTS idx_report_sample_id ON report(sample_id);
CREATE INDEX IF NOT EXISTS idx_report_customer_id ON report(customer_id);
CREATE INDEX IF NOT EXISTS idx_report_status ON report(status);
CREATE INDEX IF NOT EXISTS idx_report_create_time ON report(create_time DESC);

-- 报告审核日志表索引
CREATE INDEX IF NOT EXISTS idx_report_audit_log_report_id ON report_audit_log(report_id);
CREATE INDEX IF NOT EXISTS idx_report_audit_log_operator_id ON report_audit_log(operator_id);
CREATE INDEX IF NOT EXISTS idx_report_audit_log_audit_time ON report_audit_log(audit_time DESC);
CREATE INDEX IF NOT EXISTS idx_report_audit_log_action ON report_audit_log(action);

-- ============================================
-- 设备管理模块索引
-- ============================================

-- 设备表索引
CREATE INDEX IF NOT EXISTS idx_equipment_equipment_no ON equipment(equipment_no);
CREATE INDEX IF NOT EXISTS idx_equipment_name ON equipment(name);
CREATE INDEX IF NOT EXISTS idx_equipment_manufacturer ON equipment(manufacturer);
CREATE INDEX IF NOT EXISTS idx_equipment_status ON equipment(status);
CREATE INDEX IF NOT EXISTS idx_equipment_location ON equipment(location);
CREATE INDEX IF NOT EXISTS idx_equipment_calibration_due ON equipment(calibration_due);

-- 设备校准记录索引
CREATE INDEX IF NOT EXISTS idx_equipment_calibration_equipment_id ON equipment_calibration(equipment_id);
CREATE INDEX IF NOT EXISTS idx_equipment_calibration_calibration_date ON equipment_calibration(calibration_date DESC);
CREATE INDEX IF NOT EXISTS idx_equipment_calibration_result ON equipment_calibration(result);

-- 设备使用记录索引
CREATE INDEX IF NOT EXISTS idx_equipment_usage_equipment_id ON equipment_usage(equipment_id);
CREATE INDEX IF NOT EXISTS idx_equipment_usage_test_plan_id ON equipment_usage(test_plan_id);
CREATE INDEX IF NOT EXISTS idx_equipment_usage_user_id ON equipment_usage(user_id);
CREATE INDEX IF NOT EXISTS idx_equipment_usage_start_time ON equipment_usage(start_time DESC);
CREATE INDEX IF NOT EXISTS idx_equipment_usage_status ON equipment_usage(status);

-- ============================================
-- 人员管理模块索引
-- ============================================

-- 人员档案表索引
CREATE INDEX IF NOT EXISTS idx_personnel_user_id ON personnel(user_id);
CREATE INDEX IF NOT EXISTS idx_personnel_name ON personnel(name);
CREATE INDEX IF NOT EXISTS idx_personnel_education ON personnel(education);
CREATE INDEX IF NOT EXISTS idx_personnel_title ON personnel(title);
CREATE INDEX IF NOT EXISTS idx_personnel_status ON personnel(status);

-- 人员授权记录索引
CREATE INDEX IF NOT EXISTS idx_personnel_authorization_personnel_id ON personnel_authorization(personnel_id);
CREATE INDEX IF NOT EXISTS idx_personnel_authorization_authorization_date ON personnel_authorization(authorization_date DESC);
CREATE INDEX IF NOT EXISTS idx_personnel_authorization_expire_date ON personnel_authorization(expire_date);
-- personnel_authorization 表无 status 字段，此索引删除
-- CREATE INDEX IF NOT EXISTS idx_personnel_authorization_status ON personnel_authorization(status);

-- 人员培训记录索引
-- personnel_training 表在 init.sql 中未创建，需先创建表后再创建索引
-- CREATE INDEX IF NOT EXISTS idx_personnel_training_personnel_id ON personnel_training(personnel_id);
-- CREATE INDEX IF NOT EXISTS idx_personnel_training_train_date ON personnel_training(train_date DESC);
-- CREATE INDEX IF NOT EXISTS idx_personnel_training_course ON personnel_training(course);

-- 能力矩阵索引
-- competency_matrix 表在 init.sql 中未创建，需先创建表后再创建索引
-- CREATE INDEX IF NOT EXISTS idx_competency_matrix_personnel_id ON competency_matrix(personnel_id);
-- CREATE INDEX IF NOT EXISTS idx_competency_matrix_test_item_type ON competency_matrix(test_item_type);
-- CREATE INDEX IF NOT EXISTS idx_competency_matrix_assessment_date ON competency_matrix(assessment_date DESC);

-- ============================================
-- 标准管理模块索引
-- ============================================

-- 标准表索引
CREATE INDEX IF NOT EXISTS idx_standard_code ON standard(code);
CREATE INDEX IF NOT EXISTS idx_standard_name ON standard(name);
CREATE INDEX IF NOT EXISTS idx_standard_type ON standard(type);
CREATE INDEX IF NOT EXISTS idx_standard_status ON standard(status);
CREATE INDEX IF NOT EXISTS idx_standard_effective_date ON standard(effective_date);

-- 标准分类索引
CREATE INDEX IF NOT EXISTS idx_standard_category_name ON standard_category(name);
CREATE INDEX IF NOT EXISTS idx_standard_category_product_type ON standard_category(product_type);

-- ============================================
-- 编号规则引擎索引
-- ============================================

-- 编号规则表索引
CREATE INDEX IF NOT EXISTS idx_numbering_rule_rule_code ON sys_numbering_rule(rule_code);
CREATE INDEX IF NOT EXISTS idx_numbering_rule_status ON sys_numbering_rule(status);

-- 编号序列表索引
CREATE INDEX IF NOT EXISTS idx_numbering_sequence_rule_code ON sys_numbering_sequence(rule_code);
CREATE INDEX IF NOT EXISTS idx_numbering_sequence_date ON sys_numbering_sequence(biz_date);

-- ============================================
-- 检查索引创建结果
-- ============================================
-- 验证索引是否创建成功：
-- SELECT indexname, tablename FROM pg_indexes WHERE schemaname = 'public' AND indexname LIKE 'idx_%' ORDER BY tablename, indexname;
