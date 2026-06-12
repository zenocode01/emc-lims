import { useRef, useState } from 'react'
import { Button, Space, Tag, Modal, message, Descriptions, Drawer, Form, Input, Select, DatePicker, InputNumber } from 'antd'
import {
  PlusOutlined,
  DeleteOutlined,
  PlayCircleOutlined,
  CheckCircleOutlined,
  EyeOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons'
import type { ProColumns, ActionType } from '@ant-design/pro-components'
import { ProTable } from '@ant-design/pro-components'
import {
  testPlanApi,
  type TestPlanVO,
  type TestPlanQuery,
  TEST_PLAN_STATUS_OPTIONS,
  TEST_PLAN_STATUS_COLOR,
} from '../../api/test'

/**
 * 测试计划管理页面
 */
export default function TestPlanPage() {
  const actionRef = useRef<ActionType>(null)
  const [createOpen, setCreateOpen] = useState(false)
  const [detailOpen, setDetailOpen] = useState(false)
  const [detailData, setDetailData] = useState<TestPlanVO | undefined>(undefined)
  const [form] = Form.useForm()

  /** 新建测试计划 */
  const handleCreate = () => {
    form.resetFields()
    setCreateOpen(true)
  }

  /** 查看详情 */
  const handleDetail = async (record: TestPlanVO) => {
    try {
      const res = await testPlanApi.detail(record.id)
      setDetailData(res)
    } catch {
      setDetailData(record)
    }
    setDetailOpen(true)
  }

  /** 删除 */
  const handleDelete = async (id: number) => {
    Modal.confirm({
      title: '确认删除',
      icon: <ExclamationCircleOutlined />,
      content: '确定删除该测试计划吗？',
      okText: '确认删除',
      okType: 'danger',
      onOk: async () => {
        try {
          await testPlanApi.delete(id)
          message.success('删除成功')
          actionRef.current?.reload()
        } catch {
          // error handled
        }
      },
    })
  }

  /** 开始测试 */
  const handleStart = async (record: TestPlanVO) => {
    Modal.confirm({
      title: '开始测试',
      content: `确定开始测试计划 ${record.planNo} 吗？`,
      okText: '开始测试',
      okType: 'primary',
      onOk: async () => {
        try {
          await testPlanApi.start(record.id)
          message.success('测试已开始')
          actionRef.current?.reload()
        } catch {
          // error handled
        }
      },
    })
  }

  /** 完成测试 */
  const handleComplete = async (record: TestPlanVO) => {
    Modal.confirm({
      title: '完成测试',
      content: `确定完成测试计划 ${record.planNo} 吗？`,
      okText: '确认完成',
      okType: 'success',
      onOk: async () => {
        try {
          await testPlanApi.complete(record.id)
          message.success('测试已完成')
          actionRef.current?.reload()
        } catch {
          // error handled
        }
      },
    })
  }

  /** 获取状态标签 */
  const getStatusTag = (status: string) => {
    const option = TEST_PLAN_STATUS_OPTIONS.find((o) => o.value === status)
    const color = TEST_PLAN_STATUS_COLOR[status] || 'default'
    return <Tag color={color}>{option?.label || status}</Tag>
  }

  /** 状态变更按钮渲染 */
  const renderStatusActions = (record: TestPlanVO) => {
    const actions: { label: string; handler: () => void }[] = []

    switch (record.status) {
      case 'draft':
        actions.push({ label: '开始测试', handler: () => handleStart(record) })
        break
      case 'testing':
        actions.push({ label: '完成测试', handler: () => handleComplete(record) })
        break
      default:
        break
    }

    return actions.map((action) => (
      <a key={action.label} onClick={action.handler}>
        {action.label}
      </a>
    ))
  }

  const columns: ProColumns<TestPlanVO>[] = [
    {
      title: '计划编号',
      dataIndex: 'planNo',
      width: 160,
      search: false,
      render: (_, record) => <a onClick={() => handleDetail(record)}>{record.planNo}</a>,
    },
    {
      title: '样品编号',
      dataIndex: 'sampleNo',
      width: 150,
      search: false,
    },
    {
      title: '产品名称',
      dataIndex: 'productName',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      title: '客户',
      dataIndex: 'customerName',
      width: 120,
      search: false,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: Object.fromEntries(
        TEST_PLAN_STATUS_OPTIONS.map((o) => [o.value, { text: o.label, status: TEST_PLAN_STATUS_COLOR[o.value] === 'processing' ? 'Processing' : (TEST_PLAN_STATUS_COLOR[o.value] === 'success' ? 'Success' : 'Default') }])
      ),
      search: false,
      render: (_, record) => getStatusTag(record.status),
    },
    {
      title: '计划日期',
      dataIndex: 'planDate',
      width: 120,
      valueType: 'date',
      search: false,
    },
    {
      title: '截止日期',
      dataIndex: 'dueDate',
      width: 120,
      valueType: 'date',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      valueType: 'dateTime',
      search: false,
      sorter: true,
    },
    {
      title: '操作',
      width: 200,
      fixed: 'right',
      search: false,
      render: (_, record) => (
        <Space>
          <a onClick={() => handleDetail(record)}>
            <EyeOutlined /> 详情
          </a>
          {renderStatusActions(record)}
          {record.status === 'draft' && (
            <a onClick={() => handleDelete(record.id)} style={{ color: '#ff4d4f' }}>
              删除
            </a>
          )}
        </Space>
      ),
    },
  ]

  return (
    <Card>
      <ProTable<TestPlanVO, TestPlanQuery>
        actionRef={actionRef}
        columns={columns}
        rowKey="id"
        request={async (params) => {
          const { current, pageSize, ...rest } = params
          const res = await testPlanApi.page({
            pageNum: current,
            pageSize,
            sampleId: rest.sampleId,
            status: rest.status,
          })
          return {
            data: res.records,
            total: res.total,
            success: true,
          }
        }}
        toolbar={{
          actions: [
            <Button key="add" type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              新建测试计划
            </Button>,
          ],
        }}
        pagination={{
          showSizeChanger: true,
          showQuickJumper: true,
          pageSizeOptions: ['10', '20', '50'],
        }}
        search={{
          labelWidth: 'auto',
          defaultCollapsed: true,
        }}
        dateFormatter="string"
      />

      {/* 新建测试计划弹窗 */}
      <Modal
        title="新建测试计划"
        open={createOpen}
        onOk={() => form.submit()}
        onCancel={() => { setCreateOpen(false); form.resetFields() }}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={async (values) => {
          await testPlanApi.create(values as any)
          message.success('创建成功')
          setCreateOpen(false)
          form.resetFields()
          actionRef.current?.reload()
        }}>
          <Form.Item label="样品编号" name="sampleId" rules={[{ required: true, message: '请输入样品编号' }]}>
            <Input placeholder="请输入样品编号" />
          </Form.Item>
          <Form.Item label="客户ID" name="customerId">
            <InputNumber placeholder="请输入客户ID（可选）" />
          </Form.Item>
          <Form.Item label="测试项目ID列表" name="testItemIds" tooltip="多个项目ID用逗号分隔">
            <Input placeholder="如：1,2,3" />
          </Form.Item>
          <Form.Item label="计划日期" name="planDate">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item label="截止日期" name="dueDate">
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={2} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 详情抽屉 */}
      <Drawer
        title={`测试计划详情 - ${detailData?.planNo || ''}`}
        placement="right"
        width={700}
        open={detailOpen}
        onClose={() => setDetailOpen(false)}
      >
        {detailData && (
          <Descriptions bordered column={2} size="small">
            <Descriptions.Item label="计划编号" span={1}>{detailData.planNo}</Descriptions.Item>
            <Descriptions.Item label="状态" span={1}>{getStatusTag(detailData.status)}</Descriptions.Item>
            <Descriptions.Item label="样品编号" span={1}>{detailData.sampleNo || '-'}</Descriptions.Item>
            <Descriptions.Item label="产品名称" span={1}>{detailData.productName || '-'}</Descriptions.Item>
            <Descriptions.Item label="客户" span={1}>{detailData.customerName || '-'}</Descriptions.Item>
            <Descriptions.Item label="计划日期" span={1}>{detailData.planDate || '-'}</Descriptions.Item>
            <Descriptions.Item label="截止日期" span={1}>{detailData.dueDate || '-'}</Descriptions.Item>
            <Descriptions.Item label="备注" span={2}>{detailData.remark || '-'}</Descriptions.Item>
            <Descriptions.Item label="创建时间" span={1}>{detailData.createTime}</Descriptions.Item>
            <Descriptions.Item label="更新时间" span={1}>{detailData.updateTime}</Descriptions.Item>
          </Descriptions>
        )}
      </Drawer>
    </Card>
  )
}
