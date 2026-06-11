import React, { useState, useEffect } from 'react';
import {
  Table, Button, Space, Card, Tag, Modal, Form, Input, Select,
  InputNumber, message, Popconfirm, Typography, Row, Col, Tooltip,
} from 'antd';
import {
  PlusOutlined, EditOutlined, DeleteOutlined, PlayCircleOutlined,
  StopOutlined, ExperimentOutlined, ReloadOutlined,
} from '@ant-design/icons';
import { numberingRuleApi, NumberingRule } from '../../api/numbering-rule';

const { Text } = Typography;
const { Option } = Select;

/** 编号规则管理页面 */
const NumberingRulePage: React.FC = () => {
  const [dataSource, setDataSource] = useState<NumberingRule[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editRecord, setEditRecord] = useState<NumberingRule | null>(null);
  const [generating, setGenerating] = useState<Record<string, boolean>>({});
  const [form] = Form.useForm();

  /** 加载数据 */
  const loadData = async () => {
    setLoading(true);
    try {
      const res = await numberingRuleApi.list();
      setDataSource(res.data || []);
    } catch {
      message.error('加载编号规则失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  /** 打开新增弹窗 */
  const handleAdd = () => {
    setEditRecord(null);
    form.resetFields();
    form.setFieldsValue({
      seqLength: 4,
      separator: '-',
      datePattern: 'yyyyMMdd',
      status: 1,
    });
    setModalVisible(true);
  };

  /** 打开编辑弹窗 */
  const handleEdit = (record: NumberingRule) => {
    setEditRecord(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  /** 保存 */
  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      if (editRecord) {
        await numberingRuleApi.update({ ...editRecord, ...values });
        message.success('更新成功');
      } else {
        await numberingRuleApi.create(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      loadData();
    } catch {
      // form validation error
    }
  };

  /** 删除 */
  const handleDelete = async (id: number) => {
    try {
      await numberingRuleApi.delete(id);
      message.success('删除成功');
      loadData();
    } catch {
      message.error('删除失败');
    }
  };

  /** 启用/禁用 */
  const handleToggleStatus = async (record: NumberingRule) => {
    try {
      if (record.status === 1) {
        await numberingRuleApi.disable(record.id!);
        message.success('已禁用');
      } else {
        await numberingRuleApi.enable(record.id!);
        message.success('已启用');
      }
      loadData();
    } catch {
      message.error('操作失败');
    }
  };

  /** 测试生成编号 */
  const handleGenerate = async (ruleCode: string) => {
    setGenerating((prev) => ({ ...prev, [ruleCode]: true }));
    try {
      const res = await numberingRuleApi.generate(ruleCode);
      message.success(`生成编号: ${res.data}`);
    } catch {
      message.error('编号生成失败');
    } finally {
      setGenerating((prev) => ({ ...prev, [ruleCode]: false }));
    }
  };

  const columns = [
    {
      title: '规则编码',
      dataIndex: 'ruleCode',
      key: 'ruleCode',
      width: 160,
      render: (text: string) => <Text code>{text}</Text>,
    },
    {
      title: '规则名称',
      dataIndex: 'ruleName',
      key: 'ruleName',
      width: 150,
    },
    {
      title: '模块',
      dataIndex: 'moduleType',
      key: 'moduleType',
      width: 100,
      render: (text: string) => {
        const map: Record<string, string> = {
          sample: '样品',
          report: '报告',
          contract: '合同',
        };
        return map[text] || text;
      },
    },
    {
      title: '编号格式',
      key: 'format',
      width: 280,
      render: (_: unknown, record: NumberingRule) => {
        const parts = [record.prefix, record.datePattern, `X${'x'.repeat(record.seqLength)}`];
        return <Text code>{parts.join(record.separator)}</Text>;
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: number) =>
        status === 1 ? <Tag color="green">启用</Tag> : <Tag color="red">禁用</Tag>,
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 300,
      render: (_: unknown, record: NumberingRule) => (
        <Space size="small">
          <Tooltip title="测试生成">
            <Button
              type="link"
              size="small"
              icon={<ExperimentOutlined />}
              loading={generating[record.ruleCode]}
              onClick={() => handleGenerate(record.ruleCode)}
            >
              测试
            </Button>
          </Tooltip>
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Button
            type="link"
            size="small"
            icon={record.status === 1 ? <StopOutlined /> : <PlayCircleOutlined />}
            onClick={() => handleToggleStatus(record)}
          >
            {record.status === 1 ? '禁用' : '启用'}
          </Button>
          <Popconfirm title="确定删除此规则？" onConfirm={() => handleDelete(record.id!)}>
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card
        title="编号规则管理"
        extra={
          <Space>
            <Button icon={<ReloadOutlined />} onClick={loadData}>
              刷新
            </Button>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增规则
            </Button>
          </Space>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      </Card>

      {/* 新增/编辑弹窗 */}
      <Modal
        title={editRecord ? '编辑编号规则' : '新增编号规则'}
        open={modalVisible}
        onOk={handleSave}
        onCancel={() => setModalVisible(false)}
        width={640}
      >
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="ruleCode"
                label="规则编码"
                rules={[
                  { required: true, message: '请输入规则编码' },
                  { pattern: /^[A-Z_]+$/, message: '仅支持大写字母和下划线' },
                ]}
              >
                <Input placeholder="SAMPLE_DEFAULT" disabled={!!editRecord} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="ruleName"
                label="规则名称"
                rules={[{ required: true, message: '请输入规则名称' }]}
              >
                <Input placeholder="样品编号规则" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="moduleType"
                label="模块类型"
                rules={[{ required: true, message: '请选择模块类型' }]}
              >
                <Select placeholder="请选择">
                  <Option value="sample">样品管理</Option>
                  <Option value="report">报告管理</Option>
                  <Option value="contract">合同管理</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="description" label="描述">
                <Input placeholder="编号规则说明" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item name="prefix" label="前缀">
                <Input placeholder="EMC" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="datePattern" label="日期格式">
                <Select>
                  <Option value="yyyyMMdd">yyyyMMdd</Option>
                  <Option value="yyyyMM">yyyyMM</Option>
                  <Option value="yyMMdd">yyMMdd</Option>
                  <Option value="yyyy">yyyy</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item name="separator" label="分隔符">
                <Input placeholder="-" maxLength={2} />
              </Form.Item>
            </Col>
            <Col span={4}>
              <Form.Item
                name="seqLength"
                label="流水号位数"
                rules={[{ required: true }]}
              >
                <InputNumber min={1} max={10} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="status" label="状态">
            <Select>
              <Option value={1}>启用</Option>
              <Option value={0}>禁用</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default NumberingRulePage;
