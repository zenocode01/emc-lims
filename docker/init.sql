-- EMC LIMS 数据库初始化脚本
-- 创建时间：2026-06-10

-- 创建扩展
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- 系统管理模块
-- ============================================

-- 用户表
CREATE TABLE sys_user (
    id            BIGINT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password      VARCHAR(200) NOT NULL,
    name          VARCHAR(50) NOT NULL,
    email         VARCHAR(100),
    phone         VARCHAR(20),
    avatar        VARCHAR(500),
    gender        SMALLINT DEFAULT 0,
    status        SMALLINT DEFAULT 1,
    dept_id       BIGINT,
    role_id       BIGINT,
    birthday      DATE,
    post          VARCHAR(50),
    employee_code VARCHAR(50),
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by     BIGINT,
    update_by     BIGINT,
    deleted       SMALLINT DEFAULT 0,
    remark        VARCHAR(500)
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.gender IS '性别：0-未知，1-男，2-女';
COMMENT ON COLUMN sys_user.status IS '状态：0-禁用，1-正常';

-- 部门表
CREATE TABLE sys_dept (
    id          BIGINT PRIMARY KEY,
    parent_id   BIGINT DEFAULT 0,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(50) NOT NULL,
    sort        INT DEFAULT 0,
    status      SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

COMMENT ON TABLE sys_dept IS '部门表';

-- 角色表
CREATE TABLE sys_role (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    code        VARCHAR(50) NOT NULL UNIQUE,
    type        SMALLINT DEFAULT 2,
    status      SMALLINT DEFAULT 1,
    data_scope  SMALLINT DEFAULT 2,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.type IS '类型：1-超级管理员，2-普通角色';
COMMENT ON COLUMN sys_role.data_scope IS '数据范围：1-全部，2-本部门，3-本部门及子部门，4-仅本人';

-- 菜单/权限表
CREATE TABLE sys_menu (
    id          BIGINT PRIMARY KEY,
    parent_id   BIGINT DEFAULT 0,
    name        VARCHAR(50) NOT NULL,
    type        SMALLINT NOT NULL,
    path        VARCHAR(200),
    component   VARCHAR(200),
    permission  VARCHAR(100),
    icon        VARCHAR(50),
    sort        INT DEFAULT 0,
    status      SMALLINT DEFAULT 1,
    visible     SMALLINT DEFAULT 1,
    keep_alive  SMALLINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

COMMENT ON TABLE sys_menu IS '菜单/权限表';
COMMENT ON COLUMN sys_menu.type IS '类型：1-目录，2-菜单，3-按钮';
COMMENT ON COLUMN sys_menu.visible IS '是否可见：0-隐藏，1-显示';
COMMENT ON COLUMN sys_menu.keep_alive IS '是否缓存：0-否，1-是';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id      BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    id      BIGINT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL
);

COMMENT ON TABLE sys_role_menu IS '角色菜单关联表';

-- 数据字典表
CREATE TABLE sys_dict (
    id      BIGINT PRIMARY KEY,
    type    VARCHAR(50) NOT NULL,
    code    VARCHAR(50) NOT NULL,
    value   VARCHAR(100) NOT NULL,
    label   VARCHAR(100) NOT NULL,
    sort    INT DEFAULT 0,
    status  SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted SMALLINT DEFAULT 0,
    remark  VARCHAR(500)
);

COMMENT ON TABLE sys_dict IS '数据字典表';

-- ============================================
-- 客户管理模块
-- ============================================

-- 客户表
CREATE TABLE customer (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    type        SMALLINT DEFAULT 1,
    industry    VARCHAR(100),
    address     VARCHAR(500),
    phone       VARCHAR(20),
    email       VARCHAR(100),
    contact     VARCHAR(50),
    status      SMALLINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

COMMENT ON TABLE customer IS '客户表';
COMMENT ON COLUMN customer.type IS '类型：1-企业，2-个人';

-- ============================================
-- 客户管理模块 - 联系人
-- ============================================

-- 联系人表
CREATE TABLE customer_contact (
    id          BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    position    VARCHAR(100),
    is_primary  SMALLINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

COMMENT ON TABLE customer_contact IS '联系人表';
COMMENT ON COLUMN customer_contact.is_primary IS '是否主要联系人：0-否，1-是';

-- ============================================
-- 样品管理模块
-- ============================================

-- 样品表
CREATE TABLE sample (
    id               BIGINT PRIMARY KEY,
    sample_no        VARCHAR(50) NOT NULL UNIQUE,
    customer_id      BIGINT NOT NULL,
    contract_id      BIGINT,
    product_name     VARCHAR(200) NOT NULL,
    model            VARCHAR(100),
    manufacturer     VARCHAR(200),
    batch_no         VARCHAR(100),
    receive_date     DATE NOT NULL,
    sample_count     INT DEFAULT 1,
    status           VARCHAR(20) DEFAULT 'pending',
    test_standards   TEXT,
    test_requirements TEXT,
    tester_id        BIGINT,
    receive_by       BIGINT,
    create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by        BIGINT,
    update_by        BIGINT,
    deleted          SMALLINT DEFAULT 0,
    remark           VARCHAR(500)
);

COMMENT ON TABLE sample IS '样品表';

-- 样品照片表
CREATE TABLE sample_image (
    id         BIGINT PRIMARY KEY,
    sample_id  BIGINT NOT NULL,
    image_url  VARCHAR(500) NOT NULL,
    type       VARCHAR(20) DEFAULT 'photo',
    remark     VARCHAR(200),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sample_image IS '样品照片表';

-- 样品流转日志表
CREATE TABLE sample_log (
    id             BIGINT PRIMARY KEY,
    sample_id      BIGINT NOT NULL,
    from_status    VARCHAR(20),
    to_status      VARCHAR(20) NOT NULL,
    operator       BIGINT NOT NULL,
    operate_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark         VARCHAR(500)
);

COMMENT ON TABLE sample_log IS '样品流转日志表';

-- ============================================
-- 测试管理模块
-- ============================================

-- 测试项目定义表
CREATE TABLE test_item (
    id            BIGINT PRIMARY KEY,
    code          VARCHAR(50) NOT NULL UNIQUE,
    name          VARCHAR(200) NOT NULL,
    standard      VARCHAR(100),
    method        TEXT,
    category      VARCHAR(20) NOT NULL,
    limit_value   JSONB,
    status        SMALLINT DEFAULT 1,
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by     BIGINT,
    update_by     BIGINT,
    deleted       SMALLINT DEFAULT 0,
    remark        VARCHAR(500)
);

COMMENT ON TABLE test_item IS '测试项目定义表';
COMMENT ON COLUMN test_item.category IS '类别：emission-发射，immunity-抗扰度';

-- 测试计划表
CREATE TABLE test_plan (
    id           BIGINT PRIMARY KEY,
    plan_no      VARCHAR(50) NOT NULL UNIQUE,
    sample_id    BIGINT NOT NULL,
    customer_id  BIGINT,
    test_items   JSONB,
    status       VARCHAR(20) DEFAULT 'draft',
    plan_date    DATE,
    due_date     DATE,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by    BIGINT,
    update_by    BIGINT,
    deleted      SMALLINT DEFAULT 0,
    remark       VARCHAR(500)
);

COMMENT ON TABLE test_plan IS '测试计划表';

-- 测试数据记录表
CREATE TABLE test_record (
    id               BIGINT PRIMARY KEY,
    test_plan_id     BIGINT NOT NULL,
    test_item_id     BIGINT NOT NULL,
    tester_id        BIGINT NOT NULL,
    test_date        TIMESTAMP NOT NULL,
    result           VARCHAR(10) NOT NULL,
    measurement_value TEXT,
    limit_value      TEXT,
    margin           DECIMAL(10, 2),
    instrument_id    BIGINT,
    test_condition   TEXT,
    environment      JSONB,
    remarks          TEXT,
    create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by        BIGINT,
    update_by        BIGINT,
    deleted          SMALLINT DEFAULT 0
);

COMMENT ON TABLE test_record IS '测试数据记录表';
COMMENT ON COLUMN test_record.result IS '结果：pass-通过，fail-不通过，na-不适用';

-- 测试曲线数据表
CREATE TABLE test_curve (
    id            BIGINT PRIMARY KEY,
    test_record_id BIGINT NOT NULL,
    frequency     DECIMAL(10, 2) NOT NULL,
    amplitude     DECIMAL(10, 2) NOT NULL,
    limit         DECIMAL(10, 2),
    margin        DECIMAL(10, 2),
    marker_points JSONB,
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE test_curve IS '测试曲线数据表';

-- ============================================
-- 报告管理模块
-- ============================================

-- 报告表
CREATE TABLE report (
    id          BIGINT PRIMARY KEY,
    report_no   VARCHAR(50) NOT NULL UNIQUE,
    sample_id   BIGINT NOT NULL,
    customer_id BIGINT,
    status      VARCHAR(20) DEFAULT 'draft',
    version     INT DEFAULT 1,
    reviewer_id BIGINT,
    approver_id BIGINT,
    issued_date DATE,
    file_url    VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

COMMENT ON TABLE report IS '报告表';

-- 报告审核日志表
CREATE TABLE report_audit_log (
    id          BIGINT PRIMARY KEY,
    report_id   BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    action      VARCHAR(20) NOT NULL,
    comment     TEXT,
    audit_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE report_audit_log IS '报告审核日志表';
COMMENT ON COLUMN report_audit_log.action IS '操作：create-创建，review-审核，approve-批准，reject-打回';

-- ============================================
-- 设备管理模块
-- ============================================

-- 设备台账表
CREATE TABLE equipment (
    id              BIGINT PRIMARY KEY,
    equipment_no    VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(200) NOT NULL,
    model           VARCHAR(100),
    manufacturer    VARCHAR(200),
    serial_no       VARCHAR(100),
    location        VARCHAR(200),
    status          VARCHAR(20) DEFAULT 'normal',
    calibration_due DATE,
    last_calibration DATE,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         SMALLINT DEFAULT 0,
    remark          VARCHAR(500)
);

COMMENT ON TABLE equipment IS '设备台账表';
COMMENT ON COLUMN equipment.status IS '状态：normal-正常，maintenance-维护中，calibration-校准中，scrap-报废';

-- 校准记录表
CREATE TABLE equipment_calibration (
    id              BIGINT PRIMARY KEY,
    equipment_id    BIGINT NOT NULL,
    calibration_date DATE NOT NULL,
    due_date        DATE NOT NULL,
    calibration_org VARCHAR(200),
    certificate_no  VARCHAR(100),
    result          VARCHAR(20),
    attachment      VARCHAR(500),
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE equipment_calibration IS '设备校准记录表';

-- 设备使用记录表
CREATE TABLE equipment_usage (
    id            BIGINT PRIMARY KEY,
    equipment_id  BIGINT NOT NULL,
    test_plan_id  BIGINT,
    user_id       BIGINT NOT NULL,
    start_time    TIMESTAMP NOT NULL,
    end_time      TIMESTAMP,
    status        VARCHAR(20) DEFAULT 'in_use',
    remark        VARCHAR(500),
    create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE equipment_usage IS '设备使用记录表';

-- ============================================
-- 人员管理模块
-- ============================================

-- 人员档案表
CREATE TABLE personnel (
    id           BIGINT PRIMARY KEY,
    user_id      BIGINT,
    name         VARCHAR(50) NOT NULL,
    id_card      VARCHAR(20),
    education    VARCHAR(50),
    major        VARCHAR(100),
    title        VARCHAR(50),
    hire_date    DATE,
    status       SMALLINT DEFAULT 1,
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted      SMALLINT DEFAULT 0,
    remark       VARCHAR(500)
);

COMMENT ON TABLE personnel IS '人员档案表';

-- 授权上岗记录表
CREATE TABLE personnel_authorization (
    id              BIGINT PRIMARY KEY,
    personnel_id    BIGINT NOT NULL,
    test_item_id    BIGINT NOT NULL,
    authorize_date  DATE NOT NULL,
    expire_date     DATE,
    authorizer_id   BIGINT,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE personnel_authorization IS '授权上岗记录表';

-- ============================================
-- 标准管理模块
-- ============================================

-- 标准主表
CREATE TABLE standard (
    id              BIGINT PRIMARY KEY,
    code            VARCHAR(100) NOT NULL UNIQUE,
    name            VARCHAR(500) NOT NULL,
    version         VARCHAR(50),
    issuing_org     VARCHAR(200),
    effective_date  DATE,
    expiry_date     DATE,
    status          SMALLINT DEFAULT 1,
    type            VARCHAR(20),
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         SMALLINT DEFAULT 0,
    remark          VARCHAR(500)
);

COMMENT ON TABLE standard IS '标准主表';
COMMENT ON COLUMN standard.type IS '类型：emission-发射，immunity-抗扰度';

-- 标准分类表
CREATE TABLE standard_category (
    id                  BIGINT PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    applicable_standards JSONB,
    product_type        VARCHAR(50),
    create_time         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE standard_category IS '标准分类表';

-- ============================================
-- 初始数据
-- ============================================

-- 插入默认管理员用户（密码: admin123，实际应使用 BCrypt 加密）
INSERT INTO sys_user (id, username, password, name, gender, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1, 1);

-- 插入默认角色
INSERT INTO sys_role (id, name, code, type) VALUES
(1, '超级管理员', 'admin', 1),
(2, '检测员', 'tester', 2),
(3, '审核员', 'reviewer', 2),
(4, '报告签发人', 'approver', 2);

-- 插入用户角色关联
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 1);

-- 插入常用数据字典
INSERT INTO sys_dict (id, type, code, value, label, sort) VALUES
(1, 'sample_status', 'pending', 'pending', '待接收', 1),
(2, 'sample_status', 'received', 'received', '已接收', 2),
(3, 'sample_status', 'testing', 'testing', '测试中', 3),
(4, 'sample_status', 'completed', 'completed', '测试完成', 4),
(5, 'sample_status', 'reporting', 'reporting', '报告编制中', 5),
(6, 'sample_status', 'issued', 'issued', '已签发', 6),
(7, 'test_category', 'emission', 'emission', '发射测试', 1),
(8, 'test_category', 'immunity', 'immunity', '抗扰度测试', 2);
