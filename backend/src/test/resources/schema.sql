-- 测试用数据库表结构（H2 PostgreSQL 兼容模式）
-- 仅包含 DataPermissionLoaderTest 需要的表

CREATE TABLE IF NOT EXISTS sys_dept (
    id          BIGINT PRIMARY KEY,
    parent_id   BIGINT DEFAULT 0,
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(50) NOT NULL,
    dept_type   INT DEFAULT 1,
    leader      BIGINT,
    phone       VARCHAR(20),
    email       VARCHAR(100),
    sort        INT DEFAULT 0,
    status      SMALLINT DEFAULT 1,
    dept_id     BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT PRIMARY KEY,
    role_name   VARCHAR(50) NOT NULL,
    role_code   VARCHAR(50) NOT NULL UNIQUE,
    role_desc   VARCHAR(200),
    data_scope  SMALLINT DEFAULT 2,
    status      SMALLINT DEFAULT 1,
    sort        INT DEFAULT 0,
    dept_id     BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by   BIGINT,
    update_by   BIGINT,
    deleted     SMALLINT DEFAULT 0,
    remark      VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS sys_user (
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

CREATE TABLE IF NOT EXISTS sys_numbering_rule (
    id              BIGINT PRIMARY KEY,
    rule_code       VARCHAR(50) NOT NULL UNIQUE,
    rule_name       VARCHAR(100) NOT NULL,
    module_type     VARCHAR(50) NOT NULL,
    prefix          VARCHAR(20),
    date_pattern    VARCHAR(20),
    seq_length      INT DEFAULT 4,
    separator       VARCHAR(5) DEFAULT '-',
    description     VARCHAR(500),
    status          SMALLINT DEFAULT 1,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by       BIGINT,
    update_by       BIGINT,
    deleted         SMALLINT DEFAULT 0,
    remark          VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS sys_numbering_sequence (
    id              BIGINT PRIMARY KEY,
    rule_code       VARCHAR(50) NOT NULL,
    biz_date        DATE NOT NULL,
    current_seq     INT DEFAULT 0,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_rule_date UNIQUE (rule_code, biz_date)
);


